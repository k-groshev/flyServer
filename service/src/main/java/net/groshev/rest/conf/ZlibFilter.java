package net.groshev.rest.conf;

import com.jcraft.jzlib.ZInputStream;
import net.groshev.rest.utils.compress.CompressionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.zip.DeflaterOutputStream;

/**
 * Created by kgroshev on 02.05.16.
 */
@Component
@Order(1)
public class ZlibFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZlibFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.nanoTime();
        ServletRequest requestWrapper = new ZlibHttpServletRequestWrapper(request);
        filterChain.doFilter(requestWrapper, response);
        long endTime = System.nanoTime();
        long durationNanos = endTime - startTime;

        LOGGER.info("doFilter execution time = " + convertToMSecs(durationNanos));
    }

    private double convertToMSecs(long nanos) {
        double coeff = 1000000.0;
        return (double) nanos / coeff;
    }

}
