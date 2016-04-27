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


import java.util.Date;

import net.groshev.rest.jmx.old.ServerComponentMXBean;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedMetric;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.support.MetricType;

/**
 * <p>Title: HibernateStatsServiceMXBean</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.apmrouter.hibernate.HibernateStatsServiceMXBean</code></p>
 */

public interface HibernateStatsServiceMXBean  extends ServerComponentMXBean {

	/**
	 * 
	 * @see org.hibernate.jmx.StatisticsService#clear()
	 */
	@ManagedOperation(description = "Resets the JMX Hibernate statistics")
	public void clear();

	/**
	 * @param entityName
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getEntityStatistics(java.lang.String)
	 */
	@ManagedOperation(description = "Retrieves the Hibernate entity statistics for the passed entity name")
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "entityName", description = "The name of the Hibernate entity to acquire statistics for") })
	public OpenEntityStatisticsMBean getEntityStatistics(String entityName);

	/**
	 * @return
	 */
	@ManagedAttribute(description = "The Hibernate entity statistics")
	public OpenEntityStatisticsMBean[] getEntityStatistics();

	/**
	 * @param role
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getCollectionStatistics(java.lang.String)
	 */
	@ManagedOperation(description = "Retrieves the Hibernate collection statistics for the passed collection role")
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "role", description = "The name of the Hibernate role to acquire collection statistics for") })
	public OpenCollectionStatisticsMBean getCollectionStatistics(String role);

	/**
	 * @return
	 */
	@ManagedAttribute(description = "The Hibernate collection statistics")
	public OpenCollectionStatisticsMBean[] getCollectionStatistics();

	/**
	 * @param hql
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getQueryStatistics(java.lang.String)
	 */
	@ManagedOperation(description = "Retrieves the Hibernate query statistics for the passed sql")
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "hql", description = "The hql to retrieve the Hibernate query statistics for") })
	public OpenQueryStatisticsMBean getQueryStatistics(String hql);

	/**
	 * @return
	 */
	@ManagedAttribute(description = "The Hibernate query statistics")
	public OpenQueryStatisticsMBean[] getQueryStatistics();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getEntityDeleteCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "EntityDeleteCount", metricType = MetricType.COUNTER, description = "The total number of deleted entities")
	public long getEntityDeleteCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getEntityInsertCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "EntityInsertCount", metricType = MetricType.COUNTER, description = "The total number of inserted entities")
	public long getEntityInsertCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getEntityLoadCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "EntityLoadCount", metricType = MetricType.COUNTER, description = "The total number of loaded entities")
	public long getEntityLoadCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getEntityFetchCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "EntityFetchCount", metricType = MetricType.COUNTER, description = "The total number of fetched entities")
	public long getEntityFetchCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getEntityUpdateCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "EntityUpdateCount", metricType = MetricType.COUNTER, description = "The total number of updated entities")
	public long getEntityUpdateCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getQueryExecutionCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "QueryExecutionCount", metricType = MetricType.COUNTER, description = "The total number of executed queries")
	public long getQueryExecutionCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getQueryCacheHitCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "QueryCacheHitCount", metricType = MetricType.COUNTER, description = "The query cache hit count")
	public long getQueryCacheHitCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getQueryExecutionMaxTime()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "QueryExecutionMaxTime", metricType = MetricType.GAUGE, description = "The maximum query execution time in ms.")
	public long getQueryExecutionMaxTime();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getQueryCacheMissCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "QueryCacheMissCount", metricType = MetricType.COUNTER, description = "The query cache miss count")
	public long getQueryCacheMissCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getQueryCachePutCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "QueryCachePutCount", metricType = MetricType.COUNTER, description = "The query cache put count")
	public long getQueryCachePutCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getFlushCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "FlushCount", metricType = MetricType.COUNTER, description = "The hibernate flush count")
	public long getFlushCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getConnectCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "ConnectCount", metricType = MetricType.COUNTER, description = "The global number of connections asked by the sessions")
	public long getConnectCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getSessionCloseCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "CloseCount", metricType = MetricType.COUNTER, description = "The global number of sessions closed")
	public long getSessionCloseCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getSessionOpenCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "SessionOpenCount", metricType = MetricType.GAUGE, description = "The number of open sessions")
	public long getSessionOpenCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getCollectionLoadCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "CollectionLoadCount", metricType = MetricType.GAUGE, description = "The number of loaded collections")
	public long getCollectionLoadCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getCollectionFetchCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "CollectionFetchCount", metricType = MetricType.GAUGE, description = "The number of fetched collections")
	public long getCollectionFetchCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getCollectionUpdateCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "CollectionUpdateCount", metricType = MetricType.GAUGE, description = "The number of updated collections")
	public long getCollectionUpdateCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getCollectionRemoveCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "CollectionRemoveCount", metricType = MetricType.GAUGE, description = "The number of removed collections")
	public long getCollectionRemoveCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getCollectionRecreateCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "CollectionRecreateCount", metricType = MetricType.GAUGE, description = "The number of recreated collections")
	public long getCollectionRecreateCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getStartTime()
	 */
	@ManagedAttribute(description = "The service start timestamp")
	public long getStartTime();

	/**
	 * @return
	 */
	@ManagedAttribute(description = "The service start date")
	public Date getStartDate();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#isStatisticsEnabled()
	 */
	@ManagedAttribute(description = "The enabled state of the service")
	public boolean isStatisticsEnabled();

	/**
	 * @param enable
	 * @see org.hibernate.jmx.StatisticsService#setStatisticsEnabled(boolean)
	 */
	@ManagedAttribute(description = "The enabled state of the service")
	public void setStatisticsEnabled(boolean enable);

	/**
	 * 
	 * @see org.hibernate.jmx.StatisticsService#logSummary()
	 */
	@ManagedOperation(description = "Logs the statistics to standard out")
	public void logSummary();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getCollectionRoleNames()
	 */
	@ManagedAttribute(description = "An array of the collection role names")
	public String[] getCollectionRoleNames();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getEntityNames()
	 */
	@ManagedAttribute(description = "An array of the entity names")
	public String[] getEntityNames();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getQueries()
	 */
	@ManagedAttribute(description = "An array of the queries")
	public String[] getQueries();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getSuccessfulTransactionCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "SuccessfulTransactionCount", metricType = MetricType.COUNTER, description = "The number of successful transactions")
	public long getSuccessfulTransactionCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getTransactionCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "TransactionCount", metricType = MetricType.COUNTER, description = "The number of executed transactions")
	public long getTransactionCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getCloseStatementCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "CloseStatementCount", metricType = MetricType.COUNTER, description = "The number of closed statements")
	public long getCloseStatementCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getPrepareStatementCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "PrepareStatementCount", metricType = MetricType.COUNTER, description = "The number of prepared statements acquired")
	public long getPrepareStatementCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getOptimisticFailureCount()
	 */
	@ManagedMetric(category = "Hibernate", displayName = "OptimisticFailureCount", metricType = MetricType.COUNTER, description = "The number of optimistic failures")
	public long getOptimisticFailureCount();

	/**
	 * @return
	 * @see org.hibernate.jmx.StatisticsService#getQueryExecutionMaxTimeQueryString()
	 */
	@ManagedAttribute(description = "The HQL of the slowest query")
	public String getQueryExecutionMaxTimeQueryString();

}