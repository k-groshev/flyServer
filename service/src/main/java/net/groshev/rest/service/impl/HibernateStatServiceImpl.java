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

package net.groshev.rest.service.impl;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import net.groshev.rest.common.util.ISO8601DateParser;
import net.groshev.rest.service.HibernateStatService;
import net.groshev.rest.beans.HibernateStatBean;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.stat.CollectionStatistics;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.NaturalIdCacheStatistics;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by KGroshev on 08.12.2015.
 */
@Service
@Transactional
public class HibernateStatServiceImpl implements HibernateStatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateStatServiceImpl.class);

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public HibernateStatBean getStatistics() {
        final Statistics statistics = ((Session) entityManagerFactory.createEntityManager().getDelegate()).getSessionFactory().getStatistics();

        Map<String, QueryStatistics> queryStat = new HashMap<>();
        Arrays.asList(statistics.getQueries()).stream().forEach(e -> {
            final QueryStatistics queryStatistics = statistics.getQueryStatistics(e);
            queryStat.put(e, queryStatistics);
        });
        Map<String, EntityStatistics> entityStat = new HashMap<>();
        Arrays.asList(statistics.getEntityNames()).stream().forEach(e -> {
            final EntityStatistics entityStatistics = statistics.getEntityStatistics(e);
            entityStat.put(e, entityStatistics);
        });
        Map<String, CollectionStatistics> collectionStat = new HashMap<>();
        Arrays.asList(statistics.getCollectionRoleNames()).stream().forEach(e -> {
            final CollectionStatistics collectionStatistics = statistics.getCollectionStatistics(e);
            collectionStat.put(e, collectionStatistics);
        });
        Map<String, SecondLevelCacheStatistics> secondLevelStat = new HashMap<>();
        Arrays.asList(statistics.getSecondLevelCacheRegionNames()).stream().forEach(e -> {
            final SecondLevelCacheStatistics secondLevelCacheStatistics = statistics.getSecondLevelCacheStatistics(e);
            secondLevelStat.put(e, secondLevelCacheStatistics);
        });
        Map<String, NaturalIdCacheStatistics> naturalIdCacheStat = new HashMap<>();
        Arrays.asList(statistics.getSecondLevelCacheRegionNames()).stream().forEach(e -> {
            NaturalIdCacheStatistics naturalIdCacheStatistics = null;
            try {
                naturalIdCacheStatistics = statistics.getNaturalIdCacheStatistics(e);
            } catch (Exception e1) {
                LOGGER.info("NaturalIdCacheStatistics unavailable!", e1);
            }
            naturalIdCacheStat.put(e, naturalIdCacheStatistics);
        });
        HibernateStatBean bean = new HibernateStatBean();
        bean.setStartDate(ISO8601DateParser.toLocalDateString(new Date(statistics.getStartTime())));
        bean.setCommonStat(statistics);
        bean.setQueryStat(queryStat);
        bean.setEntityStat(entityStat);
        bean.setCollectionStat(collectionStat);
        bean.setSecondLevelStat(secondLevelStat);
        bean.setNaturalIdCacheStat(naturalIdCacheStat);
        bean.setDbString(getDBString());
        bean.setDbmsString(getDBMSString());

        return bean;
    }

    public String getDBString() {
        StringBuilder sb = new StringBuilder();

        SessionImplementor sessionImp = (SessionImplementor) entityManagerFactory.createEntityManager().getDelegate();
        DatabaseMetaData metadata = null;
        try {
            metadata = sessionImp.connection().getMetaData();
            if (metadata != null) {
                sb.append(metadata.getURL().substring(metadata.getURL().lastIndexOf('/') + 1));
                sb.append("@");
                sb.append(metadata.getURL().substring(metadata.getURL().indexOf('/') + 2, metadata.getURL().lastIndexOf('/')));
            }
        } catch (SQLException e) {
            sb.append("db error");
        }
        return sb.toString();
    }

    public String getDBMSString() {
        StringBuilder sb = new StringBuilder();

        SessionImplementor sessionImp = (SessionImplementor) entityManagerFactory.createEntityManager().getDelegate();
        DatabaseMetaData metadata = null;
        try {
            metadata = sessionImp.connection().getMetaData();
            if (metadata != null) {
                sb.append(metadata.getDatabaseProductName());
                sb.append("-");
                sb.append(metadata.getDatabaseProductVersion());
            }
        } catch (SQLException e) {
            sb.append("dbms error");
        }
        return sb.toString();
    }
}
