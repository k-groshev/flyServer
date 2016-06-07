package net.groshev.rest;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import net.groshev.rest.domain.model.FlyFile;
import net.groshev.rest.domain.model.FlyFileCounters;
import net.groshev.rest.utils.JdbcUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
public class DataLoader {
    private String hostPrimary;
    private String hostSecondary;
    private String keyspace;
    private String sqlitepath;
    private int port;

    public DataLoader() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("conf/env/mac/config.properties")) {
            prop.load(input);
            hostPrimary = prop.getProperty("cassandra.host.primary");
            hostSecondary = prop.getProperty("cassandra.host.second");
            port = Integer.valueOf(prop.getProperty("cassandra.port"));
            keyspace = prop.getProperty("cassandra.keyspace");
            sqlitepath = prop.getProperty("sqlite.path");

        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String args[]) {
        DataLoader loader = new DataLoader();
        // Connect to the cluster on localhost:9042 and keyspace "fly"
        Cluster cluster = Cluster
                .builder()
                .addContactPoint(loader.hostPrimary)
                .withPort(loader.port)
                .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
                .withLoadBalancingPolicy(
                        new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().build()))
                .build();
        Session session = cluster.connect("system");
        // drop keyspace
        session.execute("drop keyspace if exists " + loader.keyspace + ";");

        String cqlStatement = "CREATE KEYSPACE " + loader.keyspace + " WITH " +
                "replication = {'class':'SimpleStrategy','replication_factor':2}";
        session.execute(cqlStatement);
        session.close();

        session = cluster.connect(loader.keyspace);
        String cqlStatementCrtTable = "CREATE TABLE fly_file (" +
                "id bigint, " +
                "tth text, " +
                "file_size bigint, " +
                "first_date bigint, " +
                "last_date bigint, " +
                "fly_audio text," +
                "fly_audio_br text," +
                "fly_video text, " +
                "fly_xy text,  " +
                "PRIMARY KEY (tth, file_size))" +
                "WITH compression =" +
                "    { 'sstable_compression' : 'DeflateCompressor', 'chunk_length_kb' : 64 }" +
                "  AND compaction =" +
                "    { 'class' : 'LeveledCompactionStrategy' };";
        session.execute(cqlStatementCrtTable);

        cqlStatementCrtTable = "CREATE TABLE fly_file_counters (" +
                "tth text, " +
                "file_size bigint, " +
                "count_plus counter, " +
                "count_minus counter, " +
                "count_fake counter, " +
                "count_download counter," +
                "count_upload counter, " +
                "count_query counter, " +
                "count_media counter, " +
                "count_antivirus counter," +
                "PRIMARY KEY (tth, file_size));";
        session.execute(cqlStatementCrtTable);

        session.close();
        // Clean up the connection by closing it
        cluster.close();

        Connection c = null;
        Statement stmt = null;
        Cluster cluster1 = loader.getCluster();
        Session session1 = cluster1.connect(loader.keyspace);
        // Insert one record into the users table
        PreparedStatement statement = session1.prepare("INSERT INTO fly_file " +
                "(id, tth, file_size, first_date, last_date, fly_audio," +
                " fly_audio_br,  fly_video, fly_xy)" +
                "VALUES " +
                "(?,?,?,?,?,?,?,?,?);");
        BoundStatement boundStatement = new BoundStatement(statement);
        PreparedStatement statementCounters = session1.prepare("UPDATE fly_file_counters " +
                "SET count_plus = count_plus + ?, " +
                "count_minus = count_minus + ?, " +
                "count_fake = count_fake + ?, " +
                "count_download = count_download + ?," +
                "count_upload = count_upload + ?, " +
                "count_query = count_query + ?, " +
                "count_media = count_media + ?, " +
                "count_antivirus = count_antivirus + ? " +
                "WHERE tth = ? and  file_size = ?");
        BoundStatement boundStatementCounters = new BoundStatement(statementCounters);

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        long startGlobal = System.nanoTime();

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + loader.sqlitepath);
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
                flyFile.setLast_date(JdbcUtils.getOptionalLong(rs, "last_date"));

                FlyFileCounters counters = new FlyFileCounters();
                counters.setCount_plus(JdbcUtils.nvl(JdbcUtils.getOptionalLong(rs, "count_plus"), 0L));
                counters.setCount_minus(JdbcUtils.nvl(JdbcUtils.getOptionalLong(rs, "count_minus"), 0L));
                counters.setCount_fake(JdbcUtils.nvl(JdbcUtils.getOptionalLong(rs, "count_fake"), 0L));
                counters.setCount_download(JdbcUtils.nvl(JdbcUtils.getOptionalLong(rs, "count_download"), 0L));
                counters.setCount_upload(JdbcUtils.nvl(JdbcUtils.getOptionalLong(rs, "count_upload"), 0L));
                counters.setCount_query(JdbcUtils.nvl(JdbcUtils.getOptionalLong(rs, "count_query"), 1L));
                counters.setCount_media(JdbcUtils.nvl(JdbcUtils.getOptionalLong(rs, "count_media"), 0L));
                counters.setCount_antivirus(JdbcUtils.nvl(JdbcUtils.getOptionalLong(rs, "count_antivirus"), 0L));
                flyFile.setCounters(counters);

                // пишем в кассандру
                long parsePoint = System.nanoTime();
                long parse = parsePoint - start;
                loader.loadToCassandra(flyFile, session1, boundStatement, boundStatementCounters);
                long load = System.nanoTime() - parsePoint;
                counter++;
                System.out.println("loaded count = " + counter +
                    ", parse = " + formatter.format(convertToMSecs(parse)) +
                    "ms,  load = " + formatter.format(convertToMSecs(load)) + "ms");
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (ClassNotFoundException e) {
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

    public static final double convertToMSecs(final long nanos) {
        double coeff = 1000000.0;
        return (double) nanos / coeff;
    }

    private Cluster getCluster() {
        // Connect to the cluster on localhost:9042 and keyspace "fly"
        Cluster cluster = Cluster
                .builder()
                .addContactPoint(hostPrimary)
                .withPort(port)
                .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
                .withLoadBalancingPolicy(
                        new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().build()))
                .build();
        return cluster;
    }

    private void loadToCassandra(final FlyFile flyFile, Session session, BoundStatement boundStatement, BoundStatement boundStatementCounters) {
        //insert file
        session.executeAsync(boundStatement.bind(
                flyFile.getId(),
                flyFile.getTth(),
                flyFile.getFile_size(),
                flyFile.getFirst_date(),
                flyFile.getLast_date(),
                flyFile.getFly_audio(),
                flyFile.getFly_audio_br(),
                flyFile.getFly_video(),
                flyFile.getFly_xy())
        );
        //update counters
        session.executeAsync(boundStatementCounters.bind(
                flyFile.getCounters().getCount_plus(),
                flyFile.getCounters().getCount_minus(),
                flyFile.getCounters().getCount_fake(),
                flyFile.getCounters().getCount_download(),
                flyFile.getCounters().getCount_upload(),
                flyFile.getCounters().getCount_query(),
                flyFile.getCounters().getCount_media(),
                flyFile.getCounters().getCount_antivirus(),
                flyFile.getTth(),
                flyFile.getFile_size())
        );
    }

    private void closeCluster(Cluster cluster) {
        // Clean up the connection by closing it
        cluster.close();
    }

    public String getHostPrimary() {
        return this.hostPrimary;
    }

    public String getHostSecondary() {
        return this.hostSecondary;
    }

    public String getKeyspace() {
        return this.keyspace;
    }

    public int getPort() {
        return this.port;
    }
}
