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
import java.util.stream.Collectors;

public class FlyCassandraRepository implements FlyRepository {

    public static final Logger LOGGER = LoggerFactory.getLogger(FlyCassandraRepository.class);
    public static final double coeff = 1000000.0;

    private String hostPrimary = "192.168.10.11";
    private String hostSecondary = "192.168.10.12";
    private int port = 9042;
    private String keyspace = "fly";
    private Cluster cluster;
    Session session;
    PreparedStatement statementFind;
    BoundStatement boundStatementFind;

    public FlyCassandraRepository() {
        buildCluster();
        session = getCluster().connect(keyspace);
        // prepare statement
        statementFind = session.prepare("SELECT * FROM fly.fly_file WHERE  tth = :tth and file_size = :size ;");
        boundStatementFind = new BoundStatement(statementFind);

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

        String query = "update fly_file set count_query=count_query+1, last_date=toUnixTimestamp(now()) where tth in (";
        query += bean.getArray().stream()
                .map(e -> "'"+e.getTth()+ "'")
                .collect(Collectors.joining(","));
        query += ") and file_size in (";
        query += bean.getArray().stream()
                .map(e -> String.format("%d",e.getSize()))
                .collect(Collectors.joining(","));
        query += ");";


        session.executeAsync(query);

        //long end = System.nanoTime() - start;
        //LOGGER.debug("updateOne: " + end / 1000000.0 + " ms");
        return null;
    }

    @Override
    public Void update(FlyOutBean bean) {
        //long start = System.nanoTime();

        if (bean == null || bean.getId() == 0L) {
            return null;
        }

        Session session = getCluster().connect(keyspace);

        // Insert one record into the users table
        PreparedStatement statement = session.prepare("update fly_file set count_query=count_query+1, last_date=toUnixTimestamp(now()) where id=?;");

        BoundStatement boundStatement = new BoundStatement(statement);

        session.executeAsync(boundStatement.bind(
                bean.getId()));

        session.close();

        LOGGER.debug("updated id:" + bean.getId());

        //long end = System.nanoTime() - start;
        //LOGGER.debug("updateOne: " + end / 1000000.0 + " ms");
        return null;
    }

    @Override
    public FlyArrayOutBean find(FlyArrayRequestBean bean) {
        // long start = System.nanoTime();
        FlyArrayOutBean outBean = new FlyArrayOutBean();
        outBean.setArray(new ArrayList<>());
        String query = "SELECT * FROM fly.fly_file WHERE  tth in (";
        query += bean.getArray().stream()
                .map(e -> "'"+e.getTth()+ "'")
                .collect(Collectors.joining(","));
        query += ") and file_size in (";
        query += bean.getArray().stream()
                .map(e -> String.format("%d",e.getSize()))
                .collect(Collectors.joining(","));
        query += ");";

        ResultSet results = session.execute(query);
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
        }



//
//        ExecutorService pool = Executors.newFixedThreadPool(bean.getArray().size() + 2);
//        List<Callable<FlyOutBean>> callables = new ArrayList<>();
//        Callable<FlyOutBean> flyOutBeanCallable = bean.getArray().stream()
//                .map(e -> (Callable<FlyOutBean>) () -> findOne(session, e, boundStatementFind))
//                .findFirst()
//                .get();
//        callables.add(flyOutBeanCallable);
///*
//        List<Callable<FlyOutBean>> callables = new ArrayList<>();
//        bean.getArray().stream()
//                .map(e -> (Callable<FlyOutBean>) () -> findOne(session, e, boundStatementFind))
//                .findFirst()
//                .get()
//                .collect(Collectors.toList());
//*/
//
//
//        try {
//            pool.invokeAll(callables)
//                    .stream()
//                    .map(future -> {
//                        try {
//                            //long start = System.nanoTime();
//                            FlyOutBean flyOutBean = future.get();
//                            //double end = (System.nanoTime() - start) / coeff;
//                            //LOGGER.debug("find one in {} ms", end);
//                            return flyOutBean;
//                        } catch (Exception ex) {
//                            LOGGER.debug("ex:" + ex.getClass().getName() + " message:" + ex.getMessage());
//                            throw new IllegalStateException(ex);
//                        }
//                    })
//                    .forEach(flyOutBean -> outBean.getArray().add(flyOutBean));
//        } catch (InterruptedException ex) {
//            LOGGER.debug("ex:" + ex.getClass().getName() + " message:" + ex.getMessage());
//        } finally {
//            pool.shutdown();
//        }
//        //long end = System.nanoTime() - start;
        //LOGGER.debug("found (" + bean.getArray().size() + ")  in  " + end / 1000000.0 + " ms");

        return outBean;
    }

    private FlyOutBean findOne(Session session, FlyRequestBean e, BoundStatement boundStatement) {
        long start = System.nanoTime();

        ResultSet results = session.execute(boundStatement.bind()
                .setString("tth",e.getTth())
                .setLong("size",e.getSize()));
        double end = (System.nanoTime() - start) / coeff;
        LOGGER.debug("find one query {} ms", end);

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
            return mapper.map(flyFile);
        }
        return null;
    }
}
