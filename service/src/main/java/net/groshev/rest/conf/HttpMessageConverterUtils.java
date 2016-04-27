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

package net.groshev.rest.conf;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.util.ClassUtils;

public abstract class HttpMessageConverterUtils {

    private static final ClassLoader CLASSLOADER = HttpMessageConverterUtils.class.getClassLoader();

    /**
     * Returns default {@link HttpMessageConverter} instances, i.e.:
     * <p>
     * <ul>
     * <li>{@linkplain ByteArrayHttpMessageConverter}</li>
     * <li>{@linkplain StringHttpMessageConverter}</li>
     * <li>{@linkplain ResourceHttpMessageConverter}</li>
     * <li>{@linkplain Jaxb2RootElementHttpMessageConverter} (when JAXB is present)</li>
     * <li>{@linkplain MappingJackson2HttpMessageConverter} (when Jackson 2.x is present)</li>
     * <li>{@linkplain org.springframework.http.converter.json.MappingJacksonHttpMessageConverter}
     * (when Jackson 1.x is present and 2.x not)</li>
     * </ul>
     * <p>
     * <p>Note: It does not return all of the default converters defined in Spring, but just thus
     * usable for exception responses.</p>
     */
    @SuppressWarnings("deprecation")
    public static List<HttpMessageConverter<?>> getDefaultHttpMessageConverters() {

        List<HttpMessageConverter<?>> converters = new ArrayList<>();

        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringConverter.setWriteAcceptCharset(false); // See SPR-7316

        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(stringConverter);
        converters.add(new ResourceHttpMessageConverter());

        if (isJaxb2Present()) {
            converters.add(new Jaxb2RootElementHttpMessageConverter());
        }
        if (isJackson2Present()) {
            converters.add(new MappingJackson2HttpMessageConverter());

        } else if (isJacksonPresent()) {
            try {
                Class<?> clazz = Class.forName("org.springframework.http.converter.json.MappingJacksonHttpMessageConverter");
                converters.add((HttpMessageConverter<?>) clazz.newInstance());

            } catch (ClassNotFoundException ex) {
                // Ignore it, this class is not available since Spring 4.1.0.
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return converters;
    }

    /**
     * Determine whether a JAXB binder is present on the classpath and can be loaded. Will return
     * <tt>false</tt> if either the {@link javax.xml.bind.Binder} or one of its dependencies is not
     * present or cannot be loaded.
     */
    public static boolean isJaxb2Present() {
        return ClassUtils.isPresent("javax.xml.bind.Binder", CLASSLOADER);
    }

    /**
     * Determine whether Jackson 2.x is present on the classpath and can be loaded. Will return
     * <tt>false</tt> if either the {@code com.fasterxml.jackson.databind.ObjectMapper},
     * {@code com.fasterxml.jackson.core.JsonGenerator} or one of its dependencies is not present
     * or cannot be loaded.
     */
    public static boolean isJackson2Present() {
        return ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", CLASSLOADER) &&
            ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", CLASSLOADER);
    }

    /**
     * Determine whether Jackson 1.x is present on the classpath and can be loaded. Will return
     * <tt>false</tt> if either the {@code org.codehaus.jackson.map.ObjectMapper},
     * {@code org.codehaus.jackson.JsonGenerator} or one of its dependencies is not present or
     * cannot be loaded.
     */
    @Deprecated
    public static boolean isJacksonPresent() {
        return ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper", CLASSLOADER) &&
            ClassUtils.isPresent("org.codehaus.jackson.JsonGenerator", CLASSLOADER);
    }
}