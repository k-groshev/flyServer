package net.groshev.rest.domain.repository.impl;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class FlyCassandraRepository implements FlyRepository {

    public static final Logger LOGGER = LoggerFactory.getLogger(FlyCassandraRepository.class);
    public static final double coeff = 1000000.0;
    Session session;
    PreparedStatement statementFind;
    PreparedStatement statementUpdate;
    PreparedStatement statementUpdateCounters;
    private String hostPrimary = "192.168.10.11";
    private String hostSecondary = "192.168.10.12";
    private int port = 9042;
    private String keyspace = "fly";
    private Cluster cluster;

    public FlyCassandraRepository() {
        buildCluster();
        session = getCluster().connect(keyspace);
        // prepare statement
        statementFind = session.prepare("SELECT * FROM fly.fly_file WHERE  tth = ? and file_size = ?; ");
        statementUpdate = session.prepare("update fly_file set last_date=toUnixTimestamp(now()) where tth = ? and file_size = ?; ");
        statementUpdateCounters = session.prepare("update fly_file_counters set count_query=count_query+1 where tth = ? and file_size = ?; ");
    }

    private Cluster buildCluster() {
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


    public void closeCluster() {
        if (cluster != null) {
            // Clean up the connection by closing it
            session.close();
            cluster.close();
        }
    }

    @Override
    public Void insert(FlyRequestBean bean) {
        //long start = System.nanoTime();
        // Connect to the cluster

        // Insert one record into the users table
        PreparedStatement statement = session.prepare("INSERT INTO fly_file" +
                "(tth, file_size, first_date)" +
                "VALUES " +
                "(?,?,toUnixTimestamp(now()));");

        BoundStatement boundStatement = new BoundStatement(statement);

        session.executeAsync(boundStatement.bind(
                bean.getTth(),
                bean.getSize()));

        LOGGER.debug("inserted record:" + bean.toString());
        //long end = System.nanoTime() - start;
        //LOGGER.debug("insertOne: " + end / 1000000.0 + " ms");
        return null;
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

    @Override
    public Void update(FlyArrayOutBean bean) {
        //long start = System.nanoTime();

        if (bean == null || bean.getArray() == null) {
            return null;
        }
//        ExecutorService pool = Executors.newFixedThreadPool(bean.getArray().size()*2+ 2);
//        List<Callable<Void>> callables =  bean.getArray().stream()
//                .map(e -> (Callable<Void>) () -> {
//                    session.executeAsync(statementUpdate.bind(e.getTth(), e.getSize()));
//                    session.executeAsync(statementUpdateCounters.bind(e.getTth(), e.getSize()));
//                    return null;
//                })
//                .collect(Collectors.toList());
//
//        try {
//            pool.invokeAll(callables)
//                    .stream()
//                    .map(future -> {
//                        try {
//                            future.get();
//                            return null;
//                        } catch (Exception ex) {
//                            LOGGER.debug("ex:" + ex.getClass().getName() + " message:" + ex.getMessage());
//                            throw new IllegalStateException(ex);
//                        }
//                    });
//        } catch (InterruptedException ex) {
//            LOGGER.debug("ex:" + ex.getClass().getName() + " message:" + ex.getMessage());
//        } finally {
//            pool.shutdown();
//        }


        List<ResultSetFuture> features = new ArrayList<>();
        for (FlyOutBean requestBean : bean.getArray()) {
            ResultSetFuture resultSetFuture = session.executeAsync(statementUpdate.bind(requestBean.getTth(), requestBean.getSize()));
            features.add(resultSetFuture);
            ResultSetFuture resultSetFutureCounters = session.executeAsync(statementUpdateCounters.bind(requestBean.getTth(), requestBean.getSize()));
            features.add(resultSetFutureCounters);
        }

        for (ResultSetFuture feature : features) {
                feature.getUninterruptibly();
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
