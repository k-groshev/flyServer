package net.groshev.rest.domain.repository.impl;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import net.groshev.rest.beans.FlyArrayOutBean;
import net.groshev.rest.common.model.FlyFile;
import net.groshev.rest.domain.repository.FlyRepository;
import net.groshev.rest.mappers.FlyOutBeanMapper;
import net.groshev.rest.requests.FlyArrayRequestBean;
import net.groshev.rest.requests.FlyRequestBean;
import net.groshev.rest.utils.CassandraUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Created by kgroshev on 05.05.16.
 */
@Repository
public class FlyCassandraRepository implements FlyRepository {
    
    public static final Logger LOGGER = LoggerFactory.getLogger(FlyCassandraRepository.class);

    @Value("${cassandra.host}")
    private String host;
    @Value("${cassandra.port}")
    private int port;
    @Value("${cassandra.keyspace}")
    private String keyspace;

    private Cluster cluster;

    @PostConstruct
    private void initCluster() {
        buildCluster();
    }

    private Cluster buildCluster() {
        return Cluster
            .builder()
            .addContactPoint(host)
            .withPort(port)
            .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
            .withLoadBalancingPolicy(
                new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().build()))
            .build();
    }

    @PreDestroy
    private void closeCluster() {
        if (cluster != null) {
            // Clean up the connection by closing it
            cluster.close();
        }
    }

    @Override
    public Void insert(FlyRequestBean bean) {
        long start = System.nanoTime();
        // Connect to the cluster
        Session session = getCluster().connect(keyspace);

        // Insert one record into the users table
        PreparedStatement statement = session.prepare("INSERT INTO fly_file" +
            "(tth, file_size, first_date)" +
            "VALUES " +
            "(?,?,toUnixTimestamp(now()));");

        BoundStatement boundStatement = new BoundStatement(statement);

        session.executeAsync(boundStatement.bind(
            bean.getTth(),
            bean.getSize()));
        session.close();

        LOGGER.debug("inserted record:" + bean.toString());
        long end = System.nanoTime() - start;
        LOGGER.debug("insertOne: " + end / 1000000.0 + " ms");
        return null;
    }

    private Cluster getCluster() {
        long start = System.nanoTime();
        if (cluster == null) {
            cluster = buildCluster();
        }
        long end = System.nanoTime() - start;
        LOGGER.debug("cluster got in: " + end / 1000000.0 + " ms");        
        return cluster;
    }

    @Override
    public Void update(FlyArrayOutBean bean) {
        long start = System.nanoTime();

        if (bean == null || bean.getArray() == null) {
            return null;
        }

        Session session = getCluster().connect(keyspace);

        String whereClause = bean.getArray().stream()
            .map(e -> String.format("%d", e.getId()))
            .collect(Collectors.joining(","));

        session.executeAsync("update fly_file set count_query=count_query+1, last_date=toUnixTimestamp(now()) where id in(" + whereClause + ")");
        session.close();

        LOGGER.debug("updated ids:" + whereClause);

        long end = System.nanoTime() - start;
        LOGGER.debug("updateOne: " + end / 1000000.0 + " ms");
        return null;
    }

    @Override
    public FlyArrayOutBean find(FlyArrayRequestBean bean) {
        FlyArrayOutBean outBean = new FlyArrayOutBean();
        outBean.setArray(new ArrayList<>());
        // Connect to the cluster
        Session session = getCluster().connect(keyspace);
        ExecutorService pool = Executors.newFixedThreadPool(bean.getArray().size() * 2);

        try {
            final long timeoutMs = 100;
            bean.getArray().parallelStream()
                .map(e ->
                    CompletableFuture.supplyAsync(() -> {
                        ResultSet results = session.execute("SELECT * FROM fly.fly_file WHERE  tth = '" + e.getTth() + "' and file_size = " + e.getSize() + ";");
                        for (Row row : results) {

                            // получаем файл из sqlite
                            FlyFile flyFile = new FlyFile();
                            flyFile.setFly_audio(CassandraUtils.getOptionalString(row, "fly_audio"));
                            flyFile.setFly_audio_br(CassandraUtils.getOptionalString(row, "fly_audio_br"));
                            flyFile.setFly_video(CassandraUtils.getOptionalString(row, "fly_video"));
                            flyFile.setFly_xy(CassandraUtils.getOptionalString(row, "fly_xy"));

                            flyFile.setId(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row, "id"), 0L));
                            flyFile.setTth(CassandraUtils.getOptionalString(row, "tth"));
                            flyFile.setFile_size(CassandraUtils.getOptionalLong(row, "file_size"));

                            flyFile.setFirst_date(CassandraUtils.getOptionalLong(row, "first_date"));
                            flyFile.setLast_date(CassandraUtils.getOptionalString(row, "last_date"));

                            flyFile.setCount_plus(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row, "count_plus"), 0L));
                            flyFile.setCount_minus(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row, "count_minus"), 0L));
                            flyFile.setCount_fake(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row, "count_fake"), 0L));
                            flyFile.setCount_download(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row, "count_download"), 0L));
                            flyFile.setCount_upload(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row, "count_upload"), 0L));
                            flyFile.setCount_query(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row, "count_query"), 1L));
                            flyFile.setCount_media(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row, "count_media"), 0L));
                            flyFile.setCount_antivirus(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row, "count_antivirus"), 0L));
                            // пишем в кассандру

                            FlyOutBeanMapper mapper = new FlyOutBeanMapper();
                            outBean.getArray().add(mapper.map(flyFile));
                            //LOGGER.debug("got file=" + flyFile.toString());
                        }
                        return null;
                    }, pool)
                )
                .forEach(future -> {
                    try {
                        long start = System.nanoTime();
                        future.get();
                        long end = System.nanoTime() - start;
                        LOGGER.debug("findOne: " + end / 1000000.0 + " ms");
                    } catch (Exception ex) {
                        LOGGER.debug("ex:" + ex.getClass().getName() + " message:" + ex.getMessage());
                    }
                });
        } finally {
            pool.shutdown();
            session.close();
        }

        return outBean;
    }
}
