package net.groshev.rest.domain.repository.impl;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import net.groshev.rest.beans.FlyArrayOutBean;
import net.groshev.rest.common.model.FlyFile;
import net.groshev.rest.domain.repository.FlyRepository;
import net.groshev.rest.mappers.FlyOutBeanMapper;
import net.groshev.rest.requests.FlyArrayRequestBean;
import net.groshev.rest.requests.FlyRequestBean;
import net.groshev.rest.utils.CassandraUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.stream.Collectors;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

/**
 * Created by kgroshev on 05.05.16.
 */
@Repository
public class FlyCassandraRepository implements FlyRepository{

    @Value("${cassandra.host}")
    private String host;
    @Value("${cassandra.port}")
    private int port;
    @Value("${cassandra.keyspace}")
    private String keyspace;

    @Override
    public Void insert(FlyRequestBean bean) {
        // Connect to the cluster
        Cluster cluster = Cluster
                .builder()
                .addContactPoint(host)
                .withPort(port)
                .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
                .withLoadBalancingPolicy(
                        new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().build()))
                .build();
        Session session = cluster.connect(keyspace);

        // Insert one record into the users table
        PreparedStatement statement = session.prepare("INSERT INTO fly_file" +
                "(tth, file_size, first_date)" +
                "VALUES " +
                "(?,?,strftime('%s','now','localtime'));");

        BoundStatement boundStatement = new BoundStatement(statement);

        session.executeAsync(boundStatement.bind(
                bean.getTth(),
                bean.getSize()));

        // Clean up the connection by closing it
        cluster.close();

        System.out.println("inserted record:" + bean.toString());

        return null;
    }

    @Override
    public Void update(FlyArrayOutBean bean) {
        // Connect to the cluster
        Cluster cluster = Cluster
                .builder()
                .addContactPoint(host)
                .withPort(port)
                .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
                .withLoadBalancingPolicy(
                        new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().build()))
                .build();
        Session session = cluster.connect(keyspace);

        String whereClause = bean.getArray().stream()
                .map(e -> String.format("%d", e.getId()))
                .collect(Collectors.joining(","));

        session.executeAsync("update fly_file set count_query=count_query+1, last_date=strftime('%s','now','localtime') where id in(" + whereClause + ")");

        // Clean up the connection by closing it
        cluster.close();

        System.out.println("updated ids:" + whereClause);

        return null;
    }

    @Override
    public FlyArrayOutBean find(FlyArrayRequestBean bean) {
        FlyArrayOutBean outBean = new FlyArrayOutBean();
        // Connect to the cluster
        Cluster cluster = Cluster
                .builder()
                .addContactPoint(host)
                .withPort(port)
                .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
                .withLoadBalancingPolicy(
                        new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().build()))
                .build();
        Session session = cluster.connect(keyspace);


        ResultSet results = session.execute("SELECT * FROM fly_file WHERE " + getCause(bean)+";");
        for ( Row row : results ) {

            // получаем файл из sqlite
            FlyFile flyFile = new FlyFile();
            flyFile.setFly_audio(CassandraUtils.getOptionalString(row , "fly_audio"));
            flyFile.setFly_audio_br(CassandraUtils.getOptionalString(row , "fly_audio_br"));
            flyFile.setFly_video(CassandraUtils.getOptionalString(row , "fly_video"));
            flyFile.setFly_xy(CassandraUtils.getOptionalString(row , "fly_xy"));

            flyFile.setId(CassandraUtils.getOptionalLong(row , "id"));
            flyFile.setTth(CassandraUtils.getOptionalString(row , "tth"));
            flyFile.setFile_size(CassandraUtils.getOptionalLong(row , "file_size"));

            flyFile.setFirst_date(CassandraUtils.getOptionalLong(row , "first_date"));
            flyFile.setLast_date(CassandraUtils.getOptionalString(row , "last_date"));

            flyFile.setCount_plus(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row , "count_plus"), 0L));
            flyFile.setCount_minus(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row , "count_minus"), 0L));
            flyFile.setCount_fake(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row , "count_fake"), 0L));
            flyFile.setCount_download(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row , "count_download"), 0L));
            flyFile.setCount_upload(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row , "count_upload"), 0L));
            flyFile.setCount_query(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row , "count_query"), 1L));
            flyFile.setCount_media(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row , "count_media"), 0L));
            flyFile.setCount_antivirus(CassandraUtils.nvl(CassandraUtils.getOptionalLong(row , "count_antivirus"), 0L));
            // пишем в кассандру

            FlyOutBeanMapper mapper = new FlyOutBeanMapper();
            outBean.getArray().add(mapper.map(flyFile));
        }
        // Clean up the connection by closing it
        cluster.close();

        return outBean;
    }

    private String getCause(final FlyArrayRequestBean bean) {
        return bean.getArray().stream()
                .map(e -> "(tth = \"" + e.getTth() + "\" and file_size = " + e.getSize() + ")")
                .collect(Collectors.joining(" or "));
    }
}
