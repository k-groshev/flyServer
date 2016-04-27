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


import org.hibernate.stat.CollectionStatistics;
import org.hibernate.stat.internal.ConcurrentCollectionStatisticsImpl;

/**
 * <p>Title: OpenCollectionStatistics</p>
 * <p>Description: Open type wrapper for a {@link CollectionStatistics} instance </p>
 */

public class OpenCollectionStatistics implements OpenCollectionStatisticsMBean  {
	/** the delegate stats */
	protected final ConcurrentCollectionStatisticsImpl stats;

	/**
	 * Creates a new OpenCollectionStatistics
	 * @param stats the delegate stats
	 */
	public OpenCollectionStatistics(CollectionStatistics stats) {
		this.stats = (ConcurrentCollectionStatisticsImpl)stats;
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenCollectionStatisticsMBean#getLoadCount()
	 */
	@Override
	public long getLoadCount() {
		return stats.getLoadCount();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenCollectionStatisticsMBean#getFetchCount()
	 */
	@Override
	public long getFetchCount() {
		return stats.getFetchCount();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenCollectionStatisticsMBean#getCategoryName()
	 */
	@Override
	public String getCategoryName() {
		return stats.getCategoryName();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenCollectionStatisticsMBean#getRecreateCount()
	 */
	@Override
	public long getRecreateCount() {
		return stats.getRecreateCount();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenCollectionStatisticsMBean#getRemoveCount()
	 */
	@Override
	public long getRemoveCount() {
		return stats.getRemoveCount();
	}

	/**
	 * {@inheritDoc}
	 * @see net.groshev.rest.jmx.old.hibernate.OpenCollectionStatisticsMBean#getUpdateCount()
	 */
	@Override
	public long getUpdateCount() {
		return stats.getUpdateCount();
	}

}
