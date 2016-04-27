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

package net.groshev.rest.jmx.old;

import java.util.Date;

/**
 * <p>Title: ServerComponentMXBean</p>
 * <p>Description: Optional MXBean interface for MX component interfaces to extend so built in metrics are not hidden</p>
 * @author Konstantin Groshev (mail@groshev.net)
 * @version $Id$
 * @since 1.0
 */

public interface ServerComponentMXBean {
    /**
     * Returns the bean name
     * @return the bean name
     */
    public String getBeanName();

    /**
     * Called when bean is started.
     * @throws Exception thrown on any error
     */
    public void start() throws Exception;

    /**
     * Called when bean is stopped
     */
    public void stop();

    /**
     * Indicates if this component is started
     * @return true if this component is started, false otherwise
     */
    public boolean isStarted();

    /**
     * Returns the level of this instance's logger
     * @return the level of this instance's logger
     */
    public String getLevel();

    /**
     * Sets the logging level for this instance
     * @param levelName the name of the logging level for this instance
     */
    public void setLevel(String levelName);

    /**
     * Resets all the metrics
     */
    public void resetMetrics();

    /**
     * Returns the names of the metrics supported by this component
     * @return the names of the metrics supported by this component
     */
    public String[] getMetricNames();

    /**
     * Returns the UTC long timestamp of the last time the metrics were reset
     * @return a UTC long timestamp
     */
    public long getLastMetricResetTime();

    /**
     * Returns the java date timestamp of the last time the metrics were reset
     * @return a java date
     */
    public Date getLastMetricResetDate();

}
