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

/**
 * <p>Title: Methods</p>
 * <p>Description: Some static utility methods</p>
 * <p>Company: ICE Futures US</p>
 * @author Whitehead (nicholas.whitehead@theice.com)
 * @version $LastChangedRevision$
 * <p><code>org.helios.apmrouter.util.Methods</code></p>
 */

public class Methods {
    /**
     * Validates that a parameter is not null or trimmed-empty if a char sequence
     * @param <T> The type of the passed parameter
     * @param arg The argument
     * @param name An optional name of the parameter
     * @return the validated argument
     */
    public static <T> T nvl(T arg, String name) {
        if(arg==null) throw new IllegalArgumentException("The passed [" + (name==null ? "value" : name) + "] was null", new Throwable());
        if(arg instanceof CharSequence) {
            if(((CharSequence)arg).toString().trim().isEmpty()) throw new IllegalArgumentException("The passed CharSequence [" + (name==null ? "value" : name) + "] was empty", new Throwable());
        }
        return arg;
    }

    /**
     * Validates that a parameter is not null or trimmed-empty if a char sequence
     * @param <T> The type of the passed parameter
     * @param arg The argument
     * @return the validated argument
     */
    public static <T> T nvl(T arg) {
        return nvl(arg, null);
    }

}
