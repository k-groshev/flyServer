package net.groshev.rest.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        ServletRequest requestWrapper = new ZlibHttpServletRequestWrapper(request);
        filterChain.doFilter(requestWrapper, response);
    }
}
