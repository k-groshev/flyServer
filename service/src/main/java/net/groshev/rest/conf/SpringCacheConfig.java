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

import com.google.common.collect.Lists;
import net.groshev.rest.jmx.HibernateCacheStatisticsJmxRegistration;
import net.groshev.rest.jmx.SimonJmxRegistration;
import net.sf.ehcache.management.ManagementService;
import org.javasimon.Manager;
import org.javasimon.callback.Callback;
import org.javasimon.jmx.JmxRegisterCallback;
import org.javasimon.spring.ManagerFactoryBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.MBeanServerFactoryBean;

import javax.management.MBeanServer;

/**
 * настраиваем работу с кэшем объектов
 */
@Configuration
@ComponentScan("net.groshev.rest.jmx")
@EnableCaching(mode = AdviceMode.ASPECTJ)
public class SpringCacheConfig extends CachingConfigurerSupport {

    @Override
    @Bean
    public CacheManager cacheManager() {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        cacheManager.setCacheManager(ehCacheManager());
        return cacheManager;
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        ehCacheManagerFactoryBean.setShared(true);
        ehCacheManagerFactoryBean.setCacheManagerName("spring-pitfalls");
        return ehCacheManagerFactoryBean;
    }

    @Bean
    public net.sf.ehcache.CacheManager ehCacheManager() {
        return ehCacheManagerFactoryBean().getObject();
    }


    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }
    /*  -----  JMX  -----*/

    /*
    * для мониторинга ehcache при помощи jmx:
    * http://www.codesenior.com/en/tutorial/How-to-monitor-Ehcache-in-JMX-with-Spring-Configuration
    * */
    @Bean(initMethod = "init", destroyMethod = "dispose")
    public ManagementService managementService() {
        ManagementService managementService =
                new ManagementService(
                        ehCacheManager() /*cacheManager*/,
                        mBeanServer() /*MBeanServer*/,
                        true /*registerCacheManager*/,
                        true /*registerCaches*/,
                        true /*registerCacheConfigurations*/,
                        true /*registerCacheStatistics*/,
                        true /*registerCacheStores*/);
        return managementService;
    }

    @Bean
    public MBeanServer mBeanServer() {
        MBeanServerFactoryBean mBeanServerFactoryBean = new MBeanServerFactoryBean();
        mBeanServerFactoryBean.setLocateExistingServerIfPossible(true);
        mBeanServerFactoryBean.afterPropertiesSet();
        return mBeanServerFactoryBean.getObject();
    }

    @Bean
    public AnnotationMBeanExporter annotationMBeanExporter() {
        return new AnnotationMBeanExporter();
    }

    @Bean(initMethod = "register", destroyMethod = "unregister")
    public SimonJmxRegistration getSimon(){
        return new SimonJmxRegistration();
    }

    @Bean(initMethod = "register", destroyMethod = "unregister")
    public HibernateCacheStatisticsJmxRegistration getStat() {
        return new HibernateCacheStatisticsJmxRegistration();
    }

    /**
     * добавляем спринговые компоненты в simon
     */
    @Bean
    public Manager simonManager() throws Exception {
        ManagerFactoryBean managerFactoryBean = new ManagerFactoryBean();
        Callback jmxRegisterCallback = new JmxRegisterCallback(mBeanServer(), "net.groshev");
        managerFactoryBean.setCallbacks(Lists.newArrayList(jmxRegisterCallback));
        return managerFactoryBean.getObject();
    }
}
