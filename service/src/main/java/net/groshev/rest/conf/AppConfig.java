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

import java.util.Properties;
import net.groshev.rest.beans.GitRepositoryStateBean;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import static org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
//@Import({ AppSecurityConfig.class })
@ComponentScan(
    basePackages = {"net.groshev.rest"},
//        useDefaultFilters = false,
    excludeFilters = {@Filter(type = FilterType.ANNOTATION, value = Aspect.class),
//                @Filter(type = FilterType.ANNOTATION, value = Controller.class),
        @Filter(type = FilterType.REGEX, pattern = ".*Test.*")
/*
        },
        includeFilters = {
                @Filter(type = FilterType.ANNOTATION, value = Controller.class),
                @Filter(type = FilterType.ANNOTATION, value = Service.class),
                @Filter(type = FilterType.ANNOTATION, value = Repository.class),
                @Filter(type = FilterType.ANNOTATION, value = Component.class),
*/
    }
)
/*@PropertySources({
        @PropertySource("classpath:git.properties"),
        @PropertySource("classpath:conf/env/${spring.profiles.active}/dis.properties"),
        @PropertySource("classpath:conf/env/${spring.profiles.active}/hibernate.properties"),
})*/
public class AppConfig {

    @Autowired
    private Environment env;

    @Value("${dis.host}")
    private String host;
    @Value("${dis.port}")
    private String port;
    @Value("${dis.context}")
    private String context;

    /**
     * Add PropertySourcesPlaceholderConfigurer to make placeholder work.
     * This method MUST be static
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        String activeProfile;

        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        //fixme: здесь нельзя использовать Environment?
        activeProfile = System.getProperty("spring.profiles.active", "dev");

        // choose different property files for different active profile
        String classPath = "/conf/env/" + activeProfile + "/";

        // load the property file
        propertySourcesPlaceholderConfigurer.setLocations(new ClassPathResource(classPath + "hibernate.properties"),
            new ClassPathResource("git.properties"),
            new ClassPathResource(classPath + "dis.properties"));
        propertySourcesPlaceholderConfigurer.setFileEncoding("UTF-8");

        return propertySourcesPlaceholderConfigurer;
    }

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver getMultipartResolver() {
        return new CommonsMultipartResolver();
    }

    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages", "validation");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Bean(name = "siteUrl")
    public String siteUrl() {
        return host + ":" + port + "/" + context;
    }

    @Bean(name = "gitRepositoryStateBean")
    public GitRepositoryStateBean gitRepositoryStateBean() throws Exception {
        Properties p = gitPropertyFactory().getObject();
        GitRepositoryStateBean gitRepositoryStateBean = new GitRepositoryStateBean();
        if (p != null) {
            gitRepositoryStateBean.setBranch(p.getProperty("git.branch"));
            gitRepositoryStateBean.setDirty(p.getProperty("git.dirty"));
            gitRepositoryStateBean.setTags(p.getProperty("git.tags"));
            gitRepositoryStateBean.setDescribe(p.getProperty("git.commit.id.describe"));
            gitRepositoryStateBean.setShortDescribe(p.getProperty("git.commit.id.describe-short"));
            gitRepositoryStateBean.setCommitId(p.getProperty("git.commit.id"));
            gitRepositoryStateBean.setCommitIdAbbrev(p.getProperty("git.commit.id.abbrev"));
            gitRepositoryStateBean.setCommitTime(p.getProperty("git.commit.time"));
            gitRepositoryStateBean.setBuildTime(p.getProperty("git.build.time"));
            gitRepositoryStateBean.setBuildTime(p.getProperty("git.build.time"));
            gitRepositoryStateBean.setBuildUserName(p.getProperty("git.build.user.name"));
            gitRepositoryStateBean.setBuildUserEmail(p.getProperty("git.build.user.email"));
            gitRepositoryStateBean.setCommitMessageFull(p.getProperty("git.commit.message.full"));
            gitRepositoryStateBean.setCommitMessageShort(p.getProperty("git.commit.message.short"));
            gitRepositoryStateBean.setCommitUserName(p.getProperty("git.commit.user.name"));
            gitRepositoryStateBean.setCommitUserEmail(p.getProperty("git.commit.user.email"));
        }
        return gitRepositoryStateBean;
    }

    @Bean
    public PropertiesFactoryBean gitPropertyFactory() {
        PropertiesFactoryBean pF = new PropertiesFactoryBean();
        pF.setFileEncoding("UTF-8");
        pF.setLocation(new ClassPathResource("git.properties"));
        return pF;
    }
}
