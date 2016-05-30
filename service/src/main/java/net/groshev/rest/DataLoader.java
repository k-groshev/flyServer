package net.groshev.rest;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import net.groshev.rest.domain.model.FlyFile;
import net.groshev.rest.utils.JdbcUtils;

import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
public class DataLoader {
    public static final int PORT = 9042;
    public static final String ADDRESS = "192.168.10.11";
    public static final String KEYSPACE = "fly";

    public static final double convertToMSecs(final long nanos) {
        double coeff = 1000000.0;
        return (double) nanos / coeff;
    }

    public static void main(String args[])  {
        // Connect to the cluster on localhost:9042 and keyspace "fly"
        Cluster cluster = Cluster
                .builder()
                .addContactPoint(ADDRESS)
                .withPort(PORT)
                .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
                .withLoadBalancingPolicy(
                        new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().build()))
                .build();
        Session session = cluster.connect("system");

        String cqlStatement = "CREATE KEYSPACE fly WITH " +
                "replication = {'class':'SimpleStrategy','replication_factor':2}";
        session.execute(cqlStatement);
        session.close();

        session = cluster.connect(KEYSPACE);
        String cqlStatementCrtTable = "CREATE TABLE fly_file (" +
                "id bigint, " +
                "tth text, " +
                "file_size bigint, " +
                "count_plus bigint, " +
                "count_minus bigint, " +
                "count_fake bigint, " +
                "count_download bigint," +
                "count_upload bigint, " +
                "count_query bigint, " +
                "first_date bigint, " +
                "last_date text, " +
                "fly_audio text," +
                "fly_audio_br text," +
                "fly_video text, " +
                "fly_xy text,  " +
                "count_media bigint, " +
                "count_antivirus bigint," +
                "PRIMARY KEY (tth, file_size))" +
                "WITH compression =" +
                "    { 'sstable_compression' : 'DeflateCompressor', 'chunk_length_kb' : 64 }" +
                "  AND compaction =" +
                "    { 'class' : 'LeveledCompactionStrategy' };";
        session.execute(cqlStatementCrtTable);
        session.close();
        // Clean up the connection by closing it
        cluster.close();

        Connection c = null;
        Statement stmt = null;
        DataLoader loader = new DataLoader();
        Cluster cluster1 = loader.getCluster();
        Session session1 = cluster1.connect(KEYSPACE);
        // Insert one record into the users table
        PreparedStatement statement = session1.prepare("INSERT INTO fly_file" +
                "(id, tth, file_size, count_plus, count_minus, count_fake, count_download," +
                " count_upload, count_query, first_date, last_date, fly_audio," +
                " fly_audio_br,  fly_video, fly_xy,  count_media, count_antivirus)" +
                "VALUES " +
                "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
        BoundStatement boundStatement = new BoundStatement(statement);
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        long startGlobal = System.nanoTime();

        try {
            Class.forName("org.sqlite.JDBC");
            //String dbPath = "C:\\java_ee\\apps\\rest-test\\db\\fly-server-db.sqlite";
            String dbPath = "/Users/kgroshev/java_ee/apps/rest-test/db/fly-server-db.sqlite";
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM fly_file;");
            long counter = 0;
            while (rs.next()) {
                long start = System.nanoTime();
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
                long parsePoint = System.nanoTime();
                long parse = parsePoint - start;
                loader.loadToCassandra(flyFile, session1, boundStatement);
                long load = System.nanoTime() - parsePoint;
                counter++;
                System.out.println("loaded count = " + counter +
                        ", parse = " + formatter.format(convertToMSecs(parse)) +
                        "ms,  load = " + formatter.format(convertToMSecs(load)) + "ms");
            }
            rs.close();
            stmt.close();
            c.close();
        }catch (ClassNotFoundException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            session.close();
            cluster1.close();
            System.exit(0);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            session.close();
            cluster1.close();
            System.exit(0);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            session.close();
            cluster1.close();
            System.exit(0);
        }
        session.close();
        cluster1.close();
        long endGlobal = System.nanoTime() - startGlobal;
        System.out.println("Operation done successfully in " + formatter.format(convertToMSecs(endGlobal) / 1000.0) + "sec");
    }

    private Cluster getCluster() {
        // Connect to the cluster on localhost:9042 and keyspace "fly"
        Cluster cluster = Cluster
                .builder()
                .addContactPoint(ADDRESS)
                .withPort(PORT)
                .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
                .withLoadBalancingPolicy(
                        new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().build()))
                .build();
        return cluster;
    }

    private void closeCluster(Cluster cluster) {
        // Clean up the connection by closing it
        cluster.close();
    }

    private void loadToCassandra(final FlyFile flyFile, Session session, BoundStatement boundStatement) {
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
    }

}
