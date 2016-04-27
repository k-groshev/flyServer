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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import net.groshev.rest.jmx.old.APMLogLevel;
import org.apache.log4j.Logger;
import org.cliffc.high_scale_lib.Counter;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedMetric;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * <p>Title: ServerComponent</p>
 * <p>Description: A base server class with common functionality and hooks for server functions</p>
 */
@ManagedResource
public abstract class ServerComponent {
    /** Instance logger */
    protected Logger log = Logger.getLogger(getClass());
    /** The logger level */
    protected APMLogLevel level = APMLogLevel.pCode(log.getEffectiveLevel().toInt());

    /** Metrics accumulator */
    protected final NonBlockingHashMap<String, Counter> metrics = new NonBlockingHashMap<String, Counter>();
    /** The last reset time of these metrics */
    protected AtomicLong lastMetricResetTime = new AtomicLong(System.currentTimeMillis());

    /**
     * Starts this component
     * @throws Exception thrown if start fails
     */
    public void start() throws Exception {
        initCounters();
    }

    /**
     * Stops this component
     */
    public void stop() {

    }



    /**
     * Returns the level of this instance's logger
     * @return the level of this instance's logger
     */
    @ManagedAttribute(description="The logging level of this component")
    public String getLevel() {
        return level.name();
    }

    /**
     * Sets the logging level for this instance
     * @param levelName the name of the logging level for this instance
     */
    @ManagedAttribute(description="The logging level of this component")
    public void setLevel(String levelName) {
        level = APMLogLevel.valueOfName(levelName);
        log.setLevel(level.getLevel());
        info("Set Logger to level [", log.getLevel().toString(), "]");
    }

    /**
     * Issues a trace level logging request
     * @param msgs The objects to format into a log message
     */
    protected void trace(Object...msgs) {
        logAtLevel(APMLogLevel.TRACE, msgs);
    }

    /**
     * Issues a debug level logging request
     * @param msgs The objects to format into a log message
     */
    protected void debug(Object...msgs) {
        logAtLevel(APMLogLevel.DEBUG, msgs);
    }

    /**
     * Issues an info level logging request
     * @param msgs The objects to format into a log message
     */
    protected void info(Object...msgs) {
        logAtLevel(APMLogLevel.INFO, msgs);
    }

    /**
     * Issues a warn level logging request
     * @param msgs The objects to format into a log message
     */
    protected void warn(Object...msgs) {
        logAtLevel(APMLogLevel.WARN, msgs);
    }

    /**
     * Issues a error level logging request
     * @param msgs The objects to format into a log message
     */
    protected void error(Object...msgs) {
        logAtLevel(APMLogLevel.ERROR, msgs);
    }

    /**
     * Issues a fatal level logging request
     * @param msgs The objects to format into a log message
     */
    protected void fatal(Object...msgs) {
        logAtLevel(APMLogLevel.FATAL, msgs);
    }






    /**
     * Forwards the logging directive if the current level is enabled for the passed level
     * @param l The requested level
     * @param msgs The logged messages
     */
    protected void logAtLevel(APMLogLevel l, Object...msgs) {
        if(level.isEnabledFor(l)) {
            log.log(l.getLevel(), format(msgs));
        }
    }

    /**
     * Wraps the passed object in a formatted banner
     * @param objs The objects to print inside the banner
     * @return a formated banner
     */
    public static String banner(Object...objs) {
        if(objs==null || objs.length<1) return "";
        StringBuilder b = new StringBuilder("\n\t================================\n\t");
        for(Object obj: objs) {
            b.append(obj);
        }
        b.append("\n\t================================");
        return b.toString();
    }

    /**
     * Formats the passed objects into a loggable string.
     * If the last object is a {@link Throwable}, it will be formatted into a stack trace.
     * @param msgs The objects to log
     * @return the loggable string
     */
    public static String format(Object...msgs) {
        if(msgs==null||msgs.length<1) return "";
        StringBuilder b = new StringBuilder();
        int c = msgs.length-1;
        for(int i = 0; i <= c; i++) {
            if(i==c && msgs[i] instanceof Throwable) {
                b.append(formatStackTrace((Throwable)msgs[i]));
            } else {
                b.append(msgs[i]);
            }
        }
        return b.toString();
    }

    /** EOL bytes */
    private static final byte[] EOL = "\n".getBytes();

    /**
     * Formats a throwable's stack trace
     * @param t The throwable to format
     * @return the formatted stack trace
     */
    public static String formatStackTrace(Throwable t) {
        if(t==null) return "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(EOL);
            t.printStackTrace(new PrintStream(baos, true));
            baos.flush();
        } catch (IOException e) {
        }
        return baos.toString();
    }

    /**
     * Returns the metric names implemented by this component
     * @return the metric names implemented by this component
     */
    public Set<String> getSupportedMetricNames() {
        Set<String> metrics = new HashSet<String>();
        try {
            for(Method method: this.getClass().getMethods()) {
                ManagedMetric mm = method.getAnnotation(ManagedMetric.class);
                if(mm!=null) {
                    String name = mm.category();
                    if(name!=null && !name.trim().isEmpty()) {
                        metrics.add(name.trim());
                    }
                }
            }

            for(Method method: this.getClass().getDeclaredMethods()) {
                ManagedMetric mm = method.getAnnotation(ManagedMetric.class);
                if(mm!=null) {
                    String name = mm.category();
                    if(name!=null && !name.trim().isEmpty()) {
                        metrics.add(name.trim());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return metrics;

    }



    /**
     * Initializes the metric counters for this component
     */
    protected void initCounters() {
        for(String name: getSupportedMetricNames()) {
            name = name.trim();
            if(!metrics.containsKey(name)) {
                metrics.put(name, new Counter());
            }
        }
    }

    protected Counter mget(String name) {
        Counter ctr = metrics.get(name);
        if(ctr==null) {
            ctr = new Counter();
            metrics.put(name, ctr);
        }
        return ctr;
    }

    /**
     * Increments the named metric by the passed value
     * @param name The name of the metric
     * @param delta The amount to increment by
     */
    protected void incr(String name, long delta) {
        if(name==null) return;
        mget(name).add(delta);
    }

    /**
     * Decrements the named metric by the passed value
     * @param name The name of the metric
     * @param delta The amount to decrement by
     */
    protected void decr(String name, long delta) {
        if(name==null) return;
        mget(name).add(-delta);
    }

    /**
     * Decrements the named metric by 1
     * @param name The name of the metric
     */
    protected void decr(String name) {
        if(name==null) return;
        mget(name).decrement();
    }


    /**
     * Sets the named metric to the passed value
     * @param name The name of the metric
     * @param value The value to set to
     */
    protected void set(String name, long value) {
        if(name==null) return;
        mget(name).set(value);
    }


    /**
     * Increments the named metric by 1
     * @param name The name of the metric
     */
    protected void incr(String name) {
        incr(name, 1);
    }

    /**
     * Returns the value of the named metric
     * @param name The name of the metric
     * @return the value of the named metric
     */
    protected long getMetricValue(String name) {
        return mget(name).get();
    }

    /**
     * Resets all the metrics
     */
    @ManagedOperation
    public void resetMetrics() {
        for(Counter ctr: metrics.values()) {
            ctr.set(0);
        }
    }

    /**
     * Returns the names of the metrics supported by this component
     * @return the names of the metrics supported by this component
     */
    @ManagedAttribute
    public String[] getMetricNames() {
        return metrics.keySet().toArray(new String[metrics.size()]);
    }

    /**
     * Returns the UTC long timestamp of the last time the metrics were reset
     * @return a UTC long timestamp
     */
    @ManagedAttribute
    public long getLastMetricResetTime() {
        return lastMetricResetTime.get();
    }

    /**
     * Returns the java date timestamp of the last time the metrics were reset
     * @return a java date
     */
    @ManagedAttribute
    public Date getLastMetricResetDate() {
        return new Date(lastMetricResetTime.get());
    }

}
