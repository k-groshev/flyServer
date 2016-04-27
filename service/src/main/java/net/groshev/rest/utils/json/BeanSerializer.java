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

package net.groshev.rest.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.collect.Lists;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Date;
import net.groshev.rest.common.util.DateTimeFormat;
import net.groshev.rest.common.util.ISO8601DateParser;
import org.springframework.beans.BeanUtils;

/**
 * Created by mt on 26.03.2015.
 */
public class BeanSerializer extends JsonSerializer<Object> {
    private PropertyDescriptor[] propertyDescriptors;

    @Override
    public void serialize(Object t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if (propertyDescriptors == null) {
            propertyDescriptors = BeanUtils.getPropertyDescriptors(t.getClass());
        }

        jsonGenerator.writeStartObject();

        Lists.newArrayList(propertyDescriptors).forEach(propertyDescriptor -> {
            if (isSerializable(propertyDescriptor, t)) {
                try {
                    serializeProperty(propertyDescriptor, t, jsonGenerator, serializerProvider);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        });

        jsonGenerator.writeEndObject();
    }

    protected void serializeProperty(PropertyDescriptor propertyDescriptor, Object t, JsonGenerator jsonGenerator,
        SerializerProvider serializerProvider) throws Exception {
        Object propertyValue = propertyDescriptor.getReadMethod().invoke(t);
        if (propertyValue != null && propertyValue instanceof Date) {
            DateTimeFormat.Format format = DateTimeFormat.Format.DATETIME;
            DateTimeFormat dateTimeFormat = propertyDescriptor.getReadMethod().getAnnotation(DateTimeFormat.class);
            if (dateTimeFormat != null) {
                format = dateTimeFormat.value();
            }
            switch (format) {
                case DATE:
                    jsonGenerator.writeObjectField(propertyDescriptor.getName(), ISO8601DateParser.toDateString((Date) propertyValue));
                    break;
                case DATETIME:
                    jsonGenerator.writeObjectField(propertyDescriptor.getName(), ISO8601DateParser.toString((Date) propertyValue));
                    break;
            }
        } else {
            jsonGenerator.writeObjectField(propertyDescriptor.getName(), propertyValue);
        }
    }

    protected boolean isSerializable(PropertyDescriptor propertyDescriptor, Object t) {
        try {
            if ("class".equals(propertyDescriptor.getName())) {
                return false;
            }
            if (!propertyDescriptor.getPropertyType().isPrimitive()) {
                Object value = propertyDescriptor.getReadMethod().invoke(t);
                if (value == null) {
                    return false;
                }
                if (value instanceof Collection && ((Collection) value).size() == 0) {
                    return false;
                }
                if (value.getClass().isArray() && Array.getLength(value) == 0) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
