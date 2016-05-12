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

import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.status.OnConsoleStatusListener

import static ch.qos.logback.classic.Level.*

// добавляем поддержку hibernate
System.setProperty("org.jboss.logging.provider", "slf4j");
def jbossLogging = System.getProperty("org.jboss.logging.provider");
addInfo("Using logging provider for jboss: ${jbossLogging}")

scan("30 seconds")

// always a good idea to add an on console status listener
statusListener(OnConsoleStatusListener)

def BASE_DIR = System.getProperty('catalina.base') ?: System.getProperty('user.dir')
def logHome = "${BASE_DIR}/logs";
addInfo("Using logging directory: ${logHome}")

context.name = "dis"
addInfo("Context name has been set to ${context.name}")

def env = System.getProperty("spring.profiles.active","dev");
addInfo("Using enviroment: ${env}")

// поддержка JMX
jmxConfigurator()

// для раскраски логов в консоли на винде
withJansi=true;

// MDC, которые мы используем:
// user - пользователь, который залогинен

appender("stdout", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%boldWhite([%X{user}]) %highlight(%-5level) %green(%date{yyyy-MM-dd HH:mm:ss.SSS}) [%thread] %cyan(%logger): %msg%n"
    }
}

appender("async-stdout", AsyncAppender) {
    appenderRef("stdout")
    includeCallerData = true
    queueSize = 16384
}

appender("dis_all", RollingFileAppender) {
    file = "${logHome}/dis-all.log"
    encoder(PatternLayoutEncoder) {
        pattern = "[%X{user}] %-5level %date{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %logger{36}: %msg%n"
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${logHome}/archived/dis-all.%d{yyyy-MM-dd}.%i.log"
        timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP){
           maxFileSize = "10 mb"
        }
    }
}

appender("dis_error", RollingFileAppender) {
    file = "${logHome}/dis-error.log"
    encoder(PatternLayoutEncoder) {
        pattern = "[%X{user}] %-5level %date{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %logger{36}: %msg%n"
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${logHome}/archived/dis-error.%d{yyyy-MM-dd}.%i.log"
        timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP){
            maxFileSize = "10 mb"
        }
    }
}


appender("async-stdout", AsyncAppender) {
    appenderRef("stdout")
    includeCallerData = true
    queueSize = 16384
}


appenders = ["dis_all"]

logger("net.groshev", DEBUG)
logger("org.hibernate", INFO)
logger("org.postgresql", WARN)
logger("org.springframework.web.filter", INFO)
logger("org.springframework.security", INFO)
logger("org.springframework.jdbc", WARN)
logger("org.springframework.transaction", WARN)
logger("org.springframework.cache", INFO)
logger("org.springframework.aop", ERROR)
logger("net.sf.ehcache", WARN)
logger("org.codehaus.groovy", INFO)
logger("grails", WARN)
logger("grails.util.GrailsUtil", WARN)
logger("springfox", INFO)
logger("io.swagger", INFO)
logger("liquibase", INFO)

//logger("com", INFO)
//logger("org", INFO)
//logger("org.springframework", DEBUG)

if (env == "dev") {
    // отладка для hibernate и jpa
    //logger("org.hibernate", DEBUG)
    logger("org.hibernate.SQL", DEBUG)
    logger("org.hibernate.type", TRACE)
    logger("org.hibernate.hql.ast.AST", INFO)
    logger("org.hibernate.tool.hbm2ddl", WARN)
    logger("org.hibernate.hql", DEBUG)
    logger("org.hibernate.cache", INFO)
    logger("org.hibernate.jdbc", DEBUG)
    logger("org.hibernate.stat", DEBUG)
    logger("org.springframework.jdbc", INFO)
    logger("net.sf.ehcache", INFO)
    logger("liquibase", INFO)
    logger("org.springframework.data", DEBUG)
    logger("org.eclipse.jetty", INFO)
    appenders.add("stdout")
}

root(INFO, appenders)
