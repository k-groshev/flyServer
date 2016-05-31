package net.groshev.rest.domain.repository.impl;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.groshev.rest.beans.FlyArrayOutBean;
import net.groshev.rest.beans.FlyOutBean;
import net.groshev.rest.domain.model.FlyFile;
import net.groshev.rest.domain.repository.FlyRepository;
import net.groshev.rest.mappers.FlyOutBeanMapper;
import net.groshev.rest.requests.FlyArrayRequestBean;
import net.groshev.rest.requests.FlyRequestBean;
import net.groshev.rest.utils.CassandraUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlyCassandraRepository implements FlyRepository {

    public static final Logger LOGGER = LoggerFactory.getLogger(FlyCassandraRepository.class);
    public static final double coeff = 1000000.0;
    Session session;
    PreparedStatement statementFind;
    PreparedStatement statementUpdate;
    PreparedStatement statementUpdateCounters;
    PreparedStatement statementInsert;
    private String keyspace;
    private Cluster cluster;

    public FlyCassandraRepository() {
        buildCluster();
        session = getCluster().connect(keyspace);
        // prepare statements
        statementFind = session.prepare("SELECT * FROM fly.fly_file WHERE  tth = ? and file_size = ?; ");
        statementUpdate = session.prepare("update fly_file set last_date=toUnixTimestamp(now()) where tth = ? and file_size = ?; ");
        statementUpdateCounters = session.prepare("update fly_file_counters set count_query=count_query+1 where tth = ? and file_size = ?; ");
        statementInsert = session.prepare("INSERT INTO fly_file (tth, file_size, first_date) VALUES (?,?,toUnixTimestamp(now()));");
    }

    private Cluster buildCluster() {
        String hostPrimary;
        String hostSecondary;
        int port;
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("conf/env/dev/config.properties")) {
            prop.load(input);
            hostPrimary = prop.getProperty("cassandra.host.primary");
            hostSecondary = prop.getProperty("cassandra.host.second");
            port = Integer.valueOf(prop.getProperty("cassandra.port"));
            keyspace = prop.getProperty("cassandra.keyspace");

        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return Cluster
            .builder()
            .addContactPoint(hostPrimary)
            .addContactPoint(hostSecondary)
            .withPort(port)
            .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
            .withLoadBalancingPolicy(
                new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().build()))
            .build();
    }

    private Cluster getCluster() {
        //long start = System.nanoTime();
        if (cluster == null) {
            cluster = buildCluster();
        }
        //long end = System.nanoTime() - start;
        //LOGGER.debug("cluster got in: " + end / 1000000.0 + " ms");
        return cluster;
    }

    public void closeCluster() {
        if (cluster != null) {
            // Clean up the connection by closing it
            session.close();
            cluster.close();
        }
    }

    @Override
    public Void insert(FlyRequestBean bean) {
        if (bean == null) {
            return null;
        }
        session.executeAsync(statementInsert.bind(bean.getTth(),bean.getSize()));
        LOGGER.debug("inserted record:" + bean.toString());
        return null;
    }

    @Override
    public Void update(FlyArrayOutBean bean) {
        if (bean == null || bean.getArray() == null) {
            return null;
        }

        for (FlyOutBean requestBean : bean.getArray()) {
            session.executeAsync(statementUpdate.bind(requestBean.getTth(), requestBean.getSize()));
            session.executeAsync(statementUpdateCounters.bind(requestBean.getTth(), requestBean.getSize()));
        }
        return null;
    }

    @Override
    public FlyArrayOutBean find(FlyArrayRequestBean bean) {
        // long start = System.nanoTime();
        FlyArrayOutBean outBean = new FlyArrayOutBean();
        outBean.setArray(new ArrayList<>());

        List<ResultSetFuture> features = new ArrayList<>();
        for (FlyRequestBean requestBean : bean.getArray()) {
            ResultSetFuture resultSetFuture = session.executeAsync(statementFind.bind(requestBean.getTth(), requestBean.getSize()));
            features.add(resultSetFuture);
        }

        for (ResultSetFuture feature : features) {
            ResultSet rows = feature.getUninterruptibly();
            Row row = rows.one();
            if (row != null) {
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
                flyFile.setLast_date(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row, "last_date"), 0L));

                // mapping
                FlyOutBeanMapper mapper = new FlyOutBeanMapper();
                outBean.getArray().add(mapper.map(flyFile));
            }
        }
        return outBean;
    }
}
