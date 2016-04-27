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

import static net.groshev.rest.jmx.old.Methods.nvl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;


/**
 * <p>Title: APMLogLevel</p>
 * <p>Description: Enumerates the logging levels used by the apmrouter</p>
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.apmrouter.logging.APMLogLevel</code></p>
 */

public enum APMLogLevel {
    /** No filtering */
    ALL(-Integer.MAX_VALUE, Level.ALL),
    /** Highest verbosity filtering */
    TRACE(5000, Level.TRACE),
    /** High verbosity filtering */
    DEBUG(10000, Level.DEBUG),
    /** Standard verbosity filtering */
    INFO(20000, Level.INFO),
    /** Filters out all but warnings and more severe */
    WARN(30000, Level.WARN),
    /** Filters out all but errors and more severe */
    ERROR(40000, Level.ERROR),
    /** Filters out all but fatal */
    FATAL(50000, Level.FATAL),
    /** Turns off logging */
    OFF(Integer.MAX_VALUE, Level.OFF);

    /** Map of Log Levels keyed by the ordinal */
    private static final Map<Integer, APMLogLevel> ORD2ENUM;
    /** Map of Log Levels keyed by the pcode */
    private static final Map<Integer, APMLogLevel> PCODE2ENUM;

    static {
        Map<Integer, APMLogLevel> tmp = new HashMap<Integer, APMLogLevel>(APMLogLevel.values().length);
        for(APMLogLevel ll: APMLogLevel.values()) {
            tmp.put(ll.ordinal(), ll);
        }
        ORD2ENUM = Collections.unmodifiableMap(tmp);
        tmp = new HashMap<Integer, APMLogLevel>(APMLogLevel.values().length);
        for(APMLogLevel ll: APMLogLevel.values()) {
            tmp.put(ll.pCode(), ll);
        }
        PCODE2ENUM = Collections.unmodifiableMap(tmp);
    }

    /**
     * Detemrines if this log level is enabled for the passed level
     * @param level The level to compare
     * @return true if enabled, false otherwise
     */
    public boolean isEnabledFor(APMLogLevel level) {
        return level.ordinal() >= ordinal();
    }

    /**
     * Decodes the passed ordinal to a APMLogLevel.
     * Throws a runtime exception if the ordinal is invalud
     * @param ordinal The ordinal to decode
     * @return the decoded APMLogLevel
     */
    public static APMLogLevel valueOf(int ordinal) {
        APMLogLevel mt = ORD2ENUM.get(ordinal);
        if(mt==null) throw new IllegalArgumentException("The passed ordinal [" + ordinal + "] is not a valid APMLogLevel ordinal", new Throwable());
        return mt;
    }

    /**
     * Decodes the passed pCode to a APMLogLevel.
     * Throws a runtime exception if the ordinal is invalud
     * @param pCode The pCode to decode
     * @return the decoded APMLogLevel
     */
    public static APMLogLevel pCode(int pCode) {
        APMLogLevel ll = PCODE2ENUM.get(pCode);
        if(ll==null) throw new IllegalArgumentException("The passed pCode [" + pCode + "] is not a valid APMLogLevel pCode", new Throwable());
        return ll;
    }

    /**
     * Decodes the passed name to a APMLogLevel.
     * Throws a runtime exception if the ordinal is invalud
     * @param name The metricId type name to decode. Trimmed and uppercased.
     * @return the decoded APMLogLevel
     */
    public static APMLogLevel valueOfName(CharSequence name) {
        String n = nvl(name, "APMLogLevel Name").toString().trim().toUpperCase();
        try {
            return APMLogLevel.valueOf(n);
        } catch (Exception e) {
            throw new IllegalArgumentException("The passed name [" + name + "] is not a valid APMLogLevel name", new Throwable());
        }
    }


    private APMLogLevel(int pCode, Level level) {
        this.pCode = pCode;
        this.level = level;
    }

    /** The native level */
    private final Level level;
    /** The logging package native code */
    private final int pCode;

    /**
     * Returns the native level code
     * @return the native level code
     */
    public int pCode() {
        return pCode;
    }

    /**
     * Returns the native logging level
     * @return the native logging level
     */
    public Level getLevel() {
        return level;
    }
}
