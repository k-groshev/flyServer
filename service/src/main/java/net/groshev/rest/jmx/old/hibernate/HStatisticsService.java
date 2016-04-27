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

package net.groshev.rest.jmx.old.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.stat.CollectionStatistics;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.NaturalIdCacheStatistics;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;
import org.hibernate.stat.internal.ConcurrentStatisticsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JMX service for Hibernate statistics<br>
 * <br>
 * Register this MBean in your JMX server for a specific session factory
 * <pre>
 * //build the ObjectName you want
 * Hashtable tb = new Hashtable();
 * tb.put("type", "statistics");
 * tb.put("sessionFactory", "myFinancialApp");
 * ObjectName on = new ObjectName("hibernate", tb);
 * StatisticsService stats = new StatisticsService();
 * stats.setSessionFactory(sessionFactory);
 * server.registerMBean(stats, on);
 * </pre>
 * And call the MBean the way you want<br>
 * <br>
 * Register this MBean in your JMX server with no specific session factory
 * <pre>
 * //build the ObjectName you want
 * Hashtable tb = new Hashtable();
 * tb.put("type", "statistics");
 * tb.put("sessionFactory", "myFinancialApp");
 * ObjectName on = new ObjectName("hibernate", tb);
 * StatisticsService stats = new StatisticsService();
 * server.registerMBean(stats, on);
 * </pre>
 * And call the MBean by providing the <code>SessionFactoryJNDIName</code> first.
 * Then the session factory will be retrieved from JNDI and the statistics
 * loaded.
 * @author Emmanuel Bernard
 *         now deprecated See <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6190">HHH-6190</a> for details
 *
 *         поскольку в оригинале - deprecated, то  вытащилл и модифицировал
 */
public class HStatisticsService implements Statistics {

    public static final Logger LOG = LoggerFactory.getLogger(HStatisticsService.class);
    //TODO: We probably should have a StatisticsNotPublishedException, to make it clean

    SessionFactory sf;
    Statistics stats = new ConcurrentStatisticsImpl();

    /**
     * Useful to init this MBean wo a JNDI session factory name
     * @param sf session factory to register
     */
    public void setSessionFactory(SessionFactory sf) {
        this.sf = sf;
        if (sf == null) {
            stats = new ConcurrentStatisticsImpl();
        } else {
            stats = sf.getStatistics();
        }

    }

    /**
     * @see Statistics#clear()
     */
    public void clear() {
        stats.clear();
    }

    /**
     * @see Statistics#getEntityStatistics(java.lang.String)
     */
    public EntityStatistics getEntityStatistics(String entityName) {
        return stats.getEntityStatistics(entityName);
    }

    /**
     * @see Statistics#getCollectionStatistics(java.lang.String)
     */
    public CollectionStatistics getCollectionStatistics(String role) {
        return stats.getCollectionStatistics(role);
    }

    /**
     * @see Statistics#getSecondLevelCacheStatistics(java.lang.String)
     */
    public SecondLevelCacheStatistics getSecondLevelCacheStatistics(String regionName) {
        return stats.getSecondLevelCacheStatistics(regionName);
    }

    public NaturalIdCacheStatistics getNaturalIdCacheStatistics(String regionName) {
        return stats.getNaturalIdCacheStatistics(regionName);
    }

    /**
     * @see Statistics#getQueryStatistics(java.lang.String)
     */
    public QueryStatistics getQueryStatistics(String hql) {
        return stats.getQueryStatistics(hql);
    }

    /**
     * @see Statistics#getEntityDeleteCount()
     */
    public long getEntityDeleteCount() {
        return stats.getEntityDeleteCount();
    }

    /**
     * @see Statistics#getEntityInsertCount()
     */
    public long getEntityInsertCount() {
        return stats.getEntityInsertCount();
    }

    /**
     * @see Statistics#getEntityLoadCount()
     */
    public long getEntityLoadCount() {
        return stats.getEntityLoadCount();
    }

    /**
     * @see Statistics#getEntityFetchCount()
     */
    public long getEntityFetchCount() {
        return stats.getEntityFetchCount();
    }

    /**
     * @see Statistics#getEntityUpdateCount()
     */
    public long getEntityUpdateCount() {
        return stats.getEntityUpdateCount();
    }

    /**
     * @see Statistics#getQueryExecutionCount()
     */
    public long getQueryExecutionCount() {
        return stats.getQueryExecutionCount();
    }

    public long getQueryExecutionMaxTime() {
        return stats.getQueryExecutionMaxTime();
    }

    public String getQueryExecutionMaxTimeQueryString() {
        return stats.getQueryExecutionMaxTimeQueryString();
    }

    public long getQueryCacheHitCount() {
        return stats.getQueryCacheHitCount();
    }

    public long getQueryCacheMissCount() {
        return stats.getQueryCacheMissCount();
    }

    public long getQueryCachePutCount() {
        return stats.getQueryCachePutCount();
    }

    @Override
    public long getNaturalIdQueryExecutionCount() {
        return stats.getNaturalIdQueryExecutionCount();
    }

    @Override
    public long getNaturalIdQueryExecutionMaxTime() {
        return stats.getNaturalIdQueryExecutionMaxTime();
    }

    @Override
    public String getNaturalIdQueryExecutionMaxTimeRegion() {
        return stats.getNaturalIdQueryExecutionMaxTimeRegion();
    }

    public long getNaturalIdCacheHitCount() {
        return stats.getNaturalIdCacheHitCount();
    }

    public long getNaturalIdCacheMissCount() {
        return stats.getNaturalIdCacheMissCount();
    }

    public long getNaturalIdCachePutCount() {
        return stats.getNaturalIdCachePutCount();
    }

    public long getUpdateTimestampsCacheHitCount() {
        return stats.getUpdateTimestampsCacheHitCount();
    }

    public long getUpdateTimestampsCacheMissCount() {
        return stats.getUpdateTimestampsCacheMissCount();
    }

    public long getUpdateTimestampsCachePutCount() {
        return stats.getUpdateTimestampsCachePutCount();
    }

    /**
     * @see Statistics#getFlushCount()
     */
    public long getFlushCount() {
        return stats.getFlushCount();
    }

    /**
     * @see Statistics#getConnectCount()
     */
    public long getConnectCount() {
        return stats.getConnectCount();
    }

    /**
     * @see Statistics#getSecondLevelCacheHitCount()
     */
    public long getSecondLevelCacheHitCount() {
        return stats.getSecondLevelCacheHitCount();
    }

    /**
     * @see Statistics#getSecondLevelCacheMissCount()
     */
    public long getSecondLevelCacheMissCount() {
        return stats.getSecondLevelCacheMissCount();
    }

    /**
     * @see Statistics#getSecondLevelCachePutCount()
     */
    public long getSecondLevelCachePutCount() {
        return stats.getSecondLevelCachePutCount();
    }

    /**
     * @see Statistics#getSessionCloseCount()
     */
    public long getSessionCloseCount() {
        return stats.getSessionCloseCount();
    }

    /**
     * @see Statistics#getSessionOpenCount()
     */
    public long getSessionOpenCount() {
        return stats.getSessionOpenCount();
    }

    /**
     * @see Statistics#getCollectionLoadCount()
     */
    public long getCollectionLoadCount() {
        return stats.getCollectionLoadCount();
    }

    /**
     * @see Statistics#getCollectionFetchCount()
     */
    public long getCollectionFetchCount() {
        return stats.getCollectionFetchCount();
    }

    /**
     * @see Statistics#getCollectionUpdateCount()
     */
    public long getCollectionUpdateCount() {
        return stats.getCollectionUpdateCount();
    }

    /**
     * @see Statistics#getCollectionRemoveCount()
     */
    public long getCollectionRemoveCount() {
        return stats.getCollectionRemoveCount();
    }

    /**
     * @see Statistics#getCollectionRecreateCount()
     */
    public long getCollectionRecreateCount() {
        return stats.getCollectionRecreateCount();
    }

    /**
     * @see Statistics#getStartTime()
     */
    public long getStartTime() {
        return stats.getStartTime();
    }

    public void logSummary() {
        stats.logSummary();
    }

    /**
     * @see Statistics#isStatisticsEnabled()
     */
    public boolean isStatisticsEnabled() {
        return stats.isStatisticsEnabled();
    }

    /**
     * @see Statistics#setStatisticsEnabled(boolean)
     */
    public void setStatisticsEnabled(boolean enable) {
        stats.setStatisticsEnabled(enable);
    }

    public String[] getQueries() {
        return stats.getQueries();
    }

    public String[] getEntityNames() {
        return stats.getEntityNames();
    }

    public String[] getCollectionRoleNames() {
        return stats.getCollectionRoleNames();
    }

    public String[] getSecondLevelCacheRegionNames() {
        return stats.getSecondLevelCacheRegionNames();
    }

    public long getSuccessfulTransactionCount() {
        return stats.getSuccessfulTransactionCount();
    }

    public long getTransactionCount() {
        return stats.getTransactionCount();
    }

    public long getPrepareStatementCount() {
        return stats.getPrepareStatementCount();
    }

    public long getCloseStatementCount() {
        return stats.getCloseStatementCount();
    }

    public long getOptimisticFailureCount() {
        return stats.getOptimisticFailureCount();
    }
}
