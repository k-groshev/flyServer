package net.groshev.rest.utils;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import net.groshev.rest.common.model.FlyFile;

/**
 * Created with IntelliJ IDEA.
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
public class DataLoader {

    public static void main(String args[]) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String dbPath = "C:\\java_ee\\apps\\rest-test\\db\\fly-server-db.sqlite ";
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM fly_file where id < 100;");
            while (rs.next()) {

                // получаем файл из sqlite
                FlyFile flyFile = new FlyFile();
                flyFile.setFly_audio(JdbcUtils.getOptionalString(rs, "fly_audio"));
                flyFile.setFly_audio_br(JdbcUtils.getOptionalString(rs, "fly_audio_br"));
                flyFile.setFly_video(JdbcUtils.getOptionalString(rs, "fly_video"));
                flyFile.setFly_xy(JdbcUtils.getOptionalString(rs, "fly_xy"));

                flyFile.setId(JdbcUtils.getOptionalLong(rs, "id"));
                flyFile.setTth(JdbcUtils.getOptionalString(rs, "tth"));
                flyFile.setFile_size(JdbcUtils.getOptionalLong(rs, "file_size"));

                flyFile.setFirst_date(JdbcUtils.getOptionalLong(rs, "first_date"));
                flyFile.setLast_date(JdbcUtils.getOptionalString(rs, "last_date"));

                flyFile.setCount_plus(JdbcUtils.nvl(JdbcUtils.getOptionalLong(rs, "count_plus"), 0L));
                flyFile.setCount_minus(JdbcUtils.nvl(JdbcUtils.getOptionalLong(rs, "count_minus"), 0L));
                flyFile.setCount_fake(JdbcUtils.nvl(JdbcUtils.getOptionalLong(rs, "count_fake"), 0L));
                flyFile.setCount_download(JdbcUtils.nvl(JdbcUtils.getOptionalLong(rs, "count_download"), 0L));
                flyFile.setCount_upload(JdbcUtils.nvl(JdbcUtils.getOptionalLong(rs, "count_upload"), 0L));
                flyFile.setCount_query(JdbcUtils.nvl(JdbcUtils.getOptionalLong(rs, "count_query"), 1L));
                flyFile.setCount_media(JdbcUtils.nvl(JdbcUtils.getOptionalLong(rs, "count_media"), 0L));
                flyFile.setCount_antivirus(JdbcUtils.nvl(JdbcUtils.getOptionalLong(rs, "count_antivirus"), 0L));
                // пишем в кассандру
                DataLoader loader = new DataLoader();
                loader.loadToCassandra(flyFile);
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Operation done successfully");
    }

    private void loadToCassandra(final FlyFile flyFile) {
        // Connect to the cluster on localhost:9042 and keyspace "fly"
        Cluster cluster = Cluster
            .builder()
            .addContactPoint("localhost")
            .withPort(9042)
            .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
            .withLoadBalancingPolicy(
                new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().build()))
            .build();
        Session session = cluster.connect("fly");

        // Insert one record into the users table
        PreparedStatement statement = session.prepare("INSERT INTO fly_file" +
            "(id, tth, file_size, count_plus, count_minus, count_fake, count_download," +
            " count_upload, count_query, first_date, last_date, fly_audio," +
            " fly_audio_br,  fly_video, fly_xy,  count_media, count_antivirus)" +
            "VALUES " +
            "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");

        BoundStatement boundStatement = new BoundStatement(statement);

        session.executeAsync(boundStatement.bind(
            flyFile.getId(),
            flyFile.getTth(),
            flyFile.getFile_size(),
            flyFile.getCount_plus(),
            flyFile.getCount_minus(),
            flyFile.getCount_fake(),
            flyFile.getCount_download(),
            flyFile.getCount_upload(),
            flyFile.getCount_query(),
            flyFile.getFirst_date(),
            flyFile.getLast_date(),
            flyFile.getFly_audio(),
            flyFile.getFly_audio_br(),
            flyFile.getFly_video(),
            flyFile.getFly_xy(),
            flyFile.getCount_media(),
            flyFile.getCount_antivirus()));

        // Clean up the connection by closing it
        cluster.close();
    }

}
