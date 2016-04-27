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

import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement(mode = AdviceMode.PROXY)
@EnableJpaRepositories("net.groshev.rest.domain.repository")
public class SpringJpaConfig {

    @Value("${jdbc.host}")
    private String host;
    @Value("${jdbc.port}")
    private String port;
    @Value("${jdbc.base}")
    private String base;
    @Value("${jdbc.user}")
    private String user;
    @Value("${jdbc.pass}")
    private String pass;

    @Value("${hibernate.show_sql}")
    private String show_sql;
    @Value("${hibernate.format_sql}")
    private String format_sql;
    @Value("${hibernate.use_sql_comments}")
    private String use_sql_comments;
    @Value("${hibernate.hbm2ddl.auto}")
    private String hbm2ddl_auto;
    @Value("${hibernate.generate_statistics}")
    private String generate_statistics;
    @Value("${hibernate.cache.use_structured_entries}")
    private String use_structured_entries;
    @Value("${hibernate.cache.use_query_cache}")
    private String use_query_cache;
    @Value("${hibernate.cache.use_second_level_cache}")
    private String use_second_level_cache;

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory(DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(false);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQL9Dialect");
        vendorAdapter.setDatabase(Database.POSTGRESQL);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("net.groshev.rest.common.model");
        factory.setDataSource(dataSource);
        factory.setJpaProperties(jpaProperties());
        factory.afterPropertiesSet();

        return factory.getObject();
    }

    private Properties jpaProperties() {
        Properties extraProperties = new Properties();
        extraProperties.put("hibernate.hbm2ddl.auto", hbm2ddl_auto);
        extraProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");
        extraProperties.put("hibernate.show_sql", show_sql);
        extraProperties.put("hibernate.format_sql", format_sql);
        extraProperties.put("hibernate.use_sql_comments", use_sql_comments);

        extraProperties.put("hibernate.generate_statistics", generate_statistics);
        extraProperties.put("hibernate.cache.use_structured_entries", use_structured_entries);
        extraProperties.put("hibernate.cache.use_query_cache", use_query_cache);
        extraProperties.put("hibernate.cache.use_second_level_cache", use_second_level_cache);
        extraProperties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
        return extraProperties;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        JpaDialect jpaDialect = new HibernateJpaDialect();
        txManager.setEntityManagerFactory(entityManagerFactory);
        txManager.setJpaDialect(jpaDialect);
        return txManager;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://" + host + ":" + port + "/" + base);
        dataSource.setUsername(user);
        dataSource.setPassword(pass);
        return dataSource;
    }

}
