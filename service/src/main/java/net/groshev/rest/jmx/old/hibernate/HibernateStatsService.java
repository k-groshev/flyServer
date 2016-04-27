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
import net.groshev.rest.jmx.old.ServerComponentBean;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedMetric;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.support.MetricType;

/**
 * <p>Title: HibernateStatsService</p>
 * <p>Description: Wrapper for the Hibernate stats service {@link HStatisticsService} to expose internal stats as open data types</p>
 */
public class HibernateStatsService extends ServerComponentBean implements HibernateStatsServiceMXBean {
	/** The delegate stats service */
	protected final HStatisticsService delegate = new HStatisticsService();

	/**
	 * Creates a new HibernateStatsService
	 * @param sessionFactory The session factory to monitor
	 */
	public HibernateStatsService(SessionFactory sessionFactory) {
		delegate.setSessionFactory(sessionFactory);
	}


	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#clear()
	 */
	@Override
	@ManagedOperation(description="Resets the JMX Hibernate statistics")
	public void clear() {
		delegate.clear();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getEntityStatistics(java.lang.String)
	 */
	@Override
	@ManagedOperation(description="Retrieves the Hibernate entity statistics for the passed entity name")
	@ManagedOperationParameters({@ManagedOperationParameter(name="entityName", description="The name of the Hibernate entity to acquire statistics for")})
	public OpenEntityStatisticsMBean getEntityStatistics(String entityName) {
		return new OpenEntityStatistics(delegate.getEntityStatistics(entityName));
	}
	
	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getEntityStatistics()
	 */
	@Override
	@ManagedAttribute(description="The Hibernate entity statistics")
	public OpenEntityStatisticsMBean[] getEntityStatistics() {
		String[] keys = getEntityNames();
		OpenEntityStatisticsMBean[] stats = new OpenEntityStatisticsMBean[keys.length];
		for(int i = 0; i < keys.length; i++) {
			stats[i] = getEntityStatistics(keys[i]);
		}
		return stats;
	}	

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getCollectionStatistics(java.lang.String)
	 */
	@Override
	@ManagedOperation(description="Retrieves the Hibernate collection statistics for the passed collection role")
	@ManagedOperationParameters({@ManagedOperationParameter(name="role", description="The name of the Hibernate role to acquire collection statistics for")})
	public OpenCollectionStatisticsMBean getCollectionStatistics(String role) {
		return new OpenCollectionStatistics(delegate.getCollectionStatistics(role));
	}
	
	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getCollectionStatistics()
	 */
	@Override
	@ManagedAttribute(description="The Hibernate collection statistics")
	public OpenCollectionStatisticsMBean[] getCollectionStatistics() {
		String[] keys = getCollectionRoleNames();
		OpenCollectionStatisticsMBean[] stats = new OpenCollectionStatisticsMBean[keys.length];
		for(int i = 0; i < keys.length; i++) {
			stats[i] = getCollectionStatistics(keys[i]);
		}
		return stats;
	}
	

//	/**
//	 * @param regionName
//	 * @return
//	 * @see org.hibernate.jmx.StatisticsService#getSecondLevelCacheStatistics(java.lang.String)
//	 */
//	public SecondLevelCacheStatistics getSecondLevelCacheStatistics(
//			String regionName) {
//		return delegate.getSecondLevelCacheStatistics(regionName);
//	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getQueryStatistics(java.lang.String)
	 */
	@Override
	@ManagedOperation(description="Retrieves the Hibernate query statistics for the passed sql")
	@ManagedOperationParameters({@ManagedOperationParameter(name="hql", description="The hql to retrieve the Hibernate query statistics for")})
	public OpenQueryStatisticsMBean getQueryStatistics(String hql) {
		return new OpenQueryStatistics(delegate.getQueryStatistics(hql));
	}
	
	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getQueryStatistics()
	 */
	@Override
	@ManagedAttribute(description="The Hibernate query statistics")
	public OpenQueryStatisticsMBean[] getQueryStatistics() {
		String[] keys = getQueries();
		OpenQueryStatisticsMBean[] stats = new OpenQueryStatisticsMBean[keys.length];
		for(int i = 0; i < keys.length; i++) {
			stats[i] = getQueryStatistics(keys[i]);
		}
		return stats;
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getEntityDeleteCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="EntityDeleteCount", metricType=MetricType.COUNTER, description="The total number of deleted entities")
	public long getEntityDeleteCount() {
		return delegate.getEntityDeleteCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getEntityInsertCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="EntityInsertCount", metricType=MetricType.COUNTER, description="The total number of inserted entities")
	public long getEntityInsertCount() {
		return delegate.getEntityInsertCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getEntityLoadCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="EntityLoadCount", metricType=MetricType.COUNTER, description="The total number of loaded entities")
	public long getEntityLoadCount() {
		return delegate.getEntityLoadCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getEntityFetchCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="EntityFetchCount", metricType=MetricType.COUNTER, description="The total number of fetched entities")
	public long getEntityFetchCount() {
		return delegate.getEntityFetchCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getEntityUpdateCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="EntityUpdateCount", metricType=MetricType.COUNTER, description="The total number of updated entities")
	public long getEntityUpdateCount() {
		return delegate.getEntityUpdateCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getQueryExecutionCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="QueryExecutionCount", metricType=MetricType.COUNTER, description="The total number of executed queries")
	public long getQueryExecutionCount() {
		return delegate.getQueryExecutionCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getQueryCacheHitCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="QueryCacheHitCount", metricType=MetricType.COUNTER, description="The query cache hit count")
	public long getQueryCacheHitCount() {
		return delegate.getQueryCacheHitCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getQueryExecutionMaxTime()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="QueryExecutionMaxTime", metricType=MetricType.GAUGE, description="The maximum query execution time in ms.")
	public long getQueryExecutionMaxTime() {
		return delegate.getQueryExecutionMaxTime();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getQueryCacheMissCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="QueryCacheMissCount", metricType=MetricType.COUNTER, description="The query cache miss count")
	public long getQueryCacheMissCount() {
		return delegate.getQueryCacheMissCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getQueryCachePutCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="QueryCachePutCount", metricType=MetricType.COUNTER, description="The query cache put count")
	public long getQueryCachePutCount() {
		return delegate.getQueryCachePutCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getFlushCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="FlushCount", metricType=MetricType.COUNTER, description="The hibernate flush count")
	public long getFlushCount() {
		return delegate.getFlushCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getConnectCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="ConnectCount", metricType=MetricType.COUNTER, description="The global number of connections asked by the sessions")
	public long getConnectCount() {
		return delegate.getConnectCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getSessionCloseCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="CloseCount", metricType=MetricType.COUNTER, description="The global number of sessions closed")
	public long getSessionCloseCount() {
		return delegate.getSessionCloseCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getSessionOpenCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="SessionOpenCount", metricType=MetricType.COUNTER, description="The number of open sessions")
	public long getSessionOpenCount() {
		return delegate.getSessionOpenCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getCollectionLoadCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="CollectionLoadCount", metricType=MetricType.COUNTER, description="The number of loaded collections")
	public long getCollectionLoadCount() {
		return delegate.getCollectionLoadCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getCollectionFetchCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="CollectionFetchCount", metricType=MetricType.COUNTER, description="The number of fetched collections")
	public long getCollectionFetchCount() {
		return delegate.getCollectionFetchCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getCollectionUpdateCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="CollectionUpdateCount", metricType=MetricType.COUNTER, description="The number of updated collections")
	public long getCollectionUpdateCount() {
		return delegate.getCollectionUpdateCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getCollectionRemoveCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="CollectionRemoveCount", metricType= MetricType.COUNTER, description="The number of removed collections")
	public long getCollectionRemoveCount() {
		return delegate.getCollectionRemoveCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getCollectionRecreateCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="CollectionRecreateCount", metricType=MetricType.COUNTER, description="The number of recreated collections")
	public long getCollectionRecreateCount() {
		return delegate.getCollectionRecreateCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getStartTime()
	 */
	@Override
	@ManagedAttribute(description="The service start timestamp")
	public long getStartTime() {
		return delegate.getStartTime();
	}
	
	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getStartDate()
	 */
	@Override
	@ManagedAttribute(description="The service start date")
	public Date getStartDate() {
		return new Date(delegate.getStartTime());
	}
	

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#isStatisticsEnabled()
	 */
	@Override
	@ManagedAttribute(description="The enabled state of the service")
	public boolean isStatisticsEnabled() {
		return delegate.isStatisticsEnabled();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#setStatisticsEnabled(boolean)
	 */
	@Override
	@ManagedAttribute(description="The enabled state of the service")
	public void setStatisticsEnabled(boolean enable) {
		delegate.setStatisticsEnabled(enable);
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#logSummary()
	 */
	@Override
	@ManagedOperation(description="Logs the statistics to standard out")
	public void logSummary() {
		delegate.logSummary();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getCollectionRoleNames()
	 */
	@Override
	@ManagedAttribute(description="An array of the collection role names")
	public String[] getCollectionRoleNames() {
		return delegate.getCollectionRoleNames();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getEntityNames()
	 */
	@Override
	@ManagedAttribute(description="An array of the entity names")
	public String[] getEntityNames() {
		return delegate.getEntityNames();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getQueries()
	 */
	@Override
	@ManagedAttribute(description="An array of the queries")
	public String[] getQueries() {
		return delegate.getQueries();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getSuccessfulTransactionCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="SuccessfulTransactionCount", metricType=MetricType.COUNTER, description="The number of successful transactions")
	public long getSuccessfulTransactionCount() {
		return delegate.getSuccessfulTransactionCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getTransactionCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="TransactionCount", metricType=MetricType.COUNTER, description="The number of executed transactions")
	public long getTransactionCount() {
		return delegate.getTransactionCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getCloseStatementCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="CloseStatementCount", metricType=MetricType.COUNTER, description="The number of closed statements")
	public long getCloseStatementCount() {
		return delegate.getCloseStatementCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getPrepareStatementCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="PrepareStatementCount", metricType=MetricType.COUNTER, description="The number of prepared statements acquired")
	public long getPrepareStatementCount() {
		return delegate.getPrepareStatementCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getOptimisticFailureCount()
	 */
	@Override
	@ManagedMetric(category="Hibernate", displayName="OptimisticFailureCount", metricType=MetricType.COUNTER, description="The number of optimistic failures")
	public long getOptimisticFailureCount() {
		return delegate.getOptimisticFailureCount();
	}

	/**
	 * {@inheritDoc}
	 * @see  net.groshev.rest.jmx.old.hibernate..HibernateStatsServiceMXBean#getQueryExecutionMaxTimeQueryString()
	 */
	@Override
	@ManagedAttribute(description="The HQL of the slowest query")
	public String getQueryExecutionMaxTimeQueryString() {
		return delegate.getQueryExecutionMaxTimeQueryString();
	}

	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return delegate.toString();
	}


    @Override
    public void multicastEvent(final ApplicationEvent event, final ResolvableType eventType) {
        throw new UnsupportedOperationException("#multicastEvent()");

    }


}
