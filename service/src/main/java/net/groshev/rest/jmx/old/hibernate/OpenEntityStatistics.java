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


import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.internal.ConcurrentEntityStatisticsImpl;

/**
 * <p>Title: OpenEntityStatistics</p>
 * <p>Description: Open type wrapper for a {@link EntityStatistics} instance </p>
 */
public class OpenEntityStatistics implements OpenEntityStatisticsMBean {
	/** The delegate instance */
	protected final ConcurrentEntityStatisticsImpl stats;

	/**
	 * Creates a new OpenEntityStatistics
	 * @param stats The delegate {@link EntityStatistics} instance 
	 */
	public OpenEntityStatistics(EntityStatistics stats) {
		this.stats = (ConcurrentEntityStatisticsImpl)stats;
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenEntityStatisticsMBean#getDeleteCount()
	 */
	@Override
	public long getDeleteCount() {
		return stats.getDeleteCount();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenEntityStatisticsMBean#getInsertCount()
	 */
	@Override
	public long getInsertCount() {
		return stats.getInsertCount();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenEntityStatisticsMBean#getCategoryName()
	 */
	@Override
	public String getCategoryName() {
		return stats.getCategoryName();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenEntityStatisticsMBean#getLoadCount()
	 */
	@Override
	public long getLoadCount() {
		return stats.getLoadCount();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenEntityStatisticsMBean#getUpdateCount()
	 */
	@Override
	public long getUpdateCount() {
		return stats.getUpdateCount();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenEntityStatisticsMBean#getFetchCount()
	 */
	@Override
	public long getFetchCount() {
		return stats.getFetchCount();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenEntityStatisticsMBean#getOptimisticFailureCount()
	 */
	@Override
	public long getOptimisticFailureCount() {
		return stats.getOptimisticFailureCount();
	}

}
