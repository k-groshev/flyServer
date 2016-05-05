package net.groshev.rest.domain.repository.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.stream.Collectors;
import net.groshev.rest.beans.FlyArrayOutBean;
import net.groshev.rest.common.model.FlyFile;
import net.groshev.rest.domain.repository.FlyRepository;
import net.groshev.rest.mappers.FlyOutBeanMapper;
import net.groshev.rest.requests.FlyArrayRequestBean;
import net.groshev.rest.requests.FlyRequestBean;
import net.groshev.rest.utils.JdbcUtils;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */
@Repository
public class FlySqliteRepository implements FlyRepository {

    private static final String dbPath = "C:\\java_ee\\apps\\rest-test\\db\\fly-server-db.sqlite";

    @Override
    public Void insert(FlyRequestBean bean) {
        Connection c = null;
        PreparedStatement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            String sql = "insert into fly_file (tth,file_size,first_date) values(?,?,strftime('%s','now','localtime'))";
            stmt = c.prepareStatement(sql);
            stmt.setString(1, bean.getTth());
            stmt.setLong(2, bean.getSize());

            stmt.executeUpdate(); //returns the number of rows changed
            stmt.close();
            c.commit();
            c.close();
            System.out.println("inserted record:" + bean.toString());
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return null;
    }

    @Override
    public Void update(final FlyArrayOutBean bean) {

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String whereClause = bean.getArray().stream()
                .map(e -> String.format("%d", e.getId()))
                .collect(Collectors.joining(","));

            stmt.executeUpdate("update fly_file set count_query=count_query+1, last_date=strftime('%s','now','localtime') where id in(" + whereClause + ");");
            stmt.close();
            c.commit();
            c.close();
            System.out.println("updated ids:" + whereClause);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public FlyArrayOutBean find(final FlyArrayRequestBean bean) {

        FlyArrayOutBean outBean = new FlyArrayOutBean();
        outBean.setArray(new ArrayList<>());
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM fly_file where " + getCause(bean) + ";");
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

                FlyOutBeanMapper mapper = new FlyOutBeanMapper();
                outBean.getArray().add(mapper.map(flyFile));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return outBean;
    }

    private String getCause(final FlyArrayRequestBean bean) {
        return bean.getArray().stream()
            .map(e -> "(tth = \"" + e.getTth() + "\" and file_size = " + e.getSize() + ")")
            .collect(Collectors.joining(" or "));
    }

}
