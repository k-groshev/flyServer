package net.groshev.rest.utils;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by kgroshev on 05.05.16.
 */
public class CassandraUtils {

    private static final Logger logger = LoggerFactory.getLogger(CassandraUtils.class);

    // -- Handle null for primitives

    private static <T> T wasNull(Row rs, T value,  String label)  {
        if (rs.isNull(label)) {
            return null;
        }
        else {
            return value;
        }
    }

    public static Boolean getOptionalBoolean(Row rs, String label)  {
        boolean value = rs.getBool(label);
        return wasNull(rs, value, label);
    }

    public static Byte getOptionalByte(Row rs, String label)  {
        byte value = rs.getByte(label);
        return wasNull(rs, value, label);
    }

    public static String getOptionalString(Row rs, String label) {
        String value = rs.getString(label);
        return wasNull(rs, value, label);
    }

    public static Short getOptionalShort(Row rs, String label)  {
        short value = rs.getShort(label);
        return wasNull(rs, value, label);
    }

    public static Integer getOptionalInt(Row rs, String label)  {
        int value = rs.getInt(label);
        return wasNull(rs, value, label);
    }

    public static Long getOptionalLong(Row rs, String label)  {
        if (rs == null) {
            return null;
        }
        long value = rs.getLong(label);
        return wasNull(rs, value, label);
    }

    public static Float getOptionalFloat(Row rs, String label)  {
        float value = rs.getFloat(label);
        return wasNull(rs, value, label);
    }

    public static Double getOptionalDouble(Row rs, String label)  {
        double value = rs.getDouble(label);
        return wasNull(rs, value, label);
    }

    public static <T> T nvl(T value, T def) {
        return Optional.ofNullable(value).orElse(def);
    }
    public static double convertToMSecs(long nanos) {
        double coeff = 1000000.0;
        return (double) nanos / coeff;
    }
}
