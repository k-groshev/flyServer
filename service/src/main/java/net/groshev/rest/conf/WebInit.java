/*
 * Copyright (c) 2009 - 2016 groshev.net
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the groshev.net.
 * 4. Neither the name of the groshev.net nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY groshev.net ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL groshev.net BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.groshev.rest.conf;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import net.groshev.rest.security.CORSFilter;
import org.javasimon.console.SimonConsoleServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.springframework.web.util.Log4jConfigListener;

/**
 * Created by KGroshev on 27.11.2015.
 */
public class WebInit extends AbstractAnnotationConfigDispatcherServletInitializer {

    private static Logger LOGGER = LoggerFactory.getLogger(WebInit.class);

    private void createSimonWebConsole(ServletContext container) {
        ServletRegistration.Dynamic dn =
                container.addServlet("simon-webconsole", new SimonConsoleServlet());
        dn.setInitParameter("url-prefix", "/javasimon-console");
        dn.addMapping("/javasimon-console/*");
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        LOGGER.debug("onStartup on servlet");
        servletContext.addListener(new Log4jConfigListener());
        super.onStartup(servletContext);
        servletContext.addListener(new SessionListener());
        servletContext.addListener(new HttpSessionEventPublisher());
        servletContext.addListener(new RequestContextListener());
        //servletContext.setInitParameter("contextInitializerClasses", "net.groshev.dis.internal.conf.WebContextInitializer");

        createSimonWebConsole(servletContext);

    }

    @Override
    protected String getServletName() {
        return "springapp";
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setBeanName("CharacterEncodingFilter");
        characterEncodingFilter.setForceEncoding(true);

/*
    <filter - mapping >
        <filter - name > CharacterEncodingFilter </filter - name >
        <url - pattern >
</url-pattern>
        <dispatcher>ERROR</dispatcher>
        <dispatcher>REQUEST</dispatcher>

*/

        RequestContextFilter localizationFilter = new RequestContextFilter();
/*
        *     <filter>
        <filter-name>localizationFilter</filter-name>
        <filter-class>org.springframework.web.filter.RequestContextFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>localizationFilter</filter-name>
        <url-pattern>
</url-pattern>
    </filter-mapping>
        *
        *
*/

        CORSFilter corsFilter = new CORSFilter();

/*
            <filter>
        <filter-name>corsFilter</filter-name>
        <filter-class>net.groshev.dis.rest.security.CORSFilter</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>corsFilter</filter-name>
        <url-pattern>/api
</url-pattern>
    </filter-mapping>
*/

        return new Filter[]{characterEncodingFilter, localizationFilter, corsFilter};
    }

    // Этот метод должен содержать конфигурации которые инициализируют Beans
    // для инициализации бинов у нас использовалась аннотация @Bean
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{
            AppConfig.class
        };
    }

    // Тут добавляем конфигурацию, в которой инициализируем ViewResolver
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{
            WebConfig.class
        };
    }
}
