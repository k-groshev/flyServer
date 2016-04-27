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

package net.groshev.rest.jmx;

/**
 * Created with IntelliJ IDEA.
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.persistence.EntityManagerFactory;
import net.groshev.rest.jmx.old.hibernate.HibernateStatsService;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Provides code to register Hibernate's 2nd level cache statistics bean with a
 * JMX MBean server. Assumes that both the MBeanServer and the
 * EntityManagerFactory are available as Spring-managed beans. Note that while
 * registering this class enables the collection of statistics even if that was
 * previously disabled.
 */
@Component
public class HibernateCacheStatisticsJmxRegistration {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private MBeanServer mbeanServer;

    private ObjectName objectName;

    /**
     * Registers the statistics MBean that wraps a Hibernate session factory.
     *
     * @throws JMException if anything fails..
     * @see HibernateCacheStatisticsJmxRegistration#unregister()
     */
    public void register() throws JMException {
        final SessionFactory sessionFactory = ((HibernateEntityManagerFactory) entityManagerFactory).getSessionFactory();

        objectName = new ObjectName("org.hibernate:type=statistics,name=HibernateStatService");
        final HibernateStatsService statsMBean = new HibernateStatsService(sessionFactory);
        statsMBean.setStatisticsEnabled(true);
        mbeanServer.registerMBean(statsMBean, objectName);



        //старый добрый бин
        initStatistics(sessionFactory);
    }

    /**
     * Unregisters the MBean that was registered.
     *
     * @throws JMException if the de-registration fails
     * @see HibernateCacheStatisticsJmxRegistration#register()
     */
    public void unregister() throws JMException {
        mbeanServer.unregisterMBean(objectName);
        unInitStatistics();
    }

    public void initStatistics(SessionFactory sessionFactory)  throws JMException {
        ObjectName statsName = new ObjectName("org.hibernate:type=statistics, name=HibernateStatisticBean");
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

        final Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        Object statisticsMBean = Proxy.newProxyInstance(getClass().getClassLoader(),
            new Class<?>[] { HibernateStatisticsMXBean.class },
            (proxy, method, args) -> method.invoke(statistics, args));

        mbeanServer.registerMBean(statisticsMBean, statsName);
    }
    public void unInitStatistics() throws JMException {
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        mbeanServer.unregisterMBean(new ObjectName("org.hibernate:type=statistics, name=HibernateStatisticBean"));
    }
}