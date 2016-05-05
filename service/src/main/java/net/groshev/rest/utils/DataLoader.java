package net.groshev.rest.utils;

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

                //System.out.println(flyFile.toString());
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

}
