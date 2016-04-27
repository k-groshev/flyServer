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


import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.internal.ConcurrentQueryStatisticsImpl;

/**
 * <p>Title: OpenQueryStatistics</p>
 * <p>Description: Open type wrapper for a {@link QueryStatistics} instance </p>
 */

public class OpenQueryStatistics implements OpenQueryStatisticsMBean {
	/** The delegate stats */
	protected final ConcurrentQueryStatisticsImpl stats;

	/**
	 * Creates a new OpenQueryStatistics
	 * @param stats the delegate stats
	 */
	public OpenQueryStatistics(QueryStatistics stats) {
		this.stats = (ConcurrentQueryStatisticsImpl)stats;
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenQueryStatisticsMBean#getCategoryName()
	 */
	@Override
	public String getCategoryName() {
		return stats.getCategoryName();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenQueryStatisticsMBean#getExecutionCount()
	 */
	@Override
	public long getExecutionCount() {
		return stats.getExecutionCount();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenQueryStatisticsMBean#getCacheHitCount()
	 */
	@Override
	public long getCacheHitCount() {
		return stats.getCacheHitCount();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenQueryStatisticsMBean#getCachePutCount()
	 */
	@Override
	public long getCachePutCount() {
		return stats.getCachePutCount();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenQueryStatisticsMBean#getCacheMissCount()
	 */
	@Override
	public long getCacheMissCount() {
		return stats.getCacheMissCount();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenQueryStatisticsMBean#getExecutionRowCount()
	 */
	@Override
	public long getExecutionRowCount() {
		return stats.getExecutionRowCount();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenQueryStatisticsMBean#getExecutionAvgTime()
	 */
	@Override
	public long getExecutionAvgTime() {
		return stats.getExecutionAvgTime();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenQueryStatisticsMBean#getExecutionMaxTime()
	 */
	@Override
	public long getExecutionMaxTime() {
		return stats.getExecutionMaxTime();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenQueryStatisticsMBean#getExecutionMinTime()
	 */
	@Override
	public long getExecutionMinTime() {
		return stats.getExecutionMinTime();
	}

}
