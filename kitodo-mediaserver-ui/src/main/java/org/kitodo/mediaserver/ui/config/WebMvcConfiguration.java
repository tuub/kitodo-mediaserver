/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * LICENSE file that was distributed with this source code.
 */

package org.kitodo.mediaserver.ui.config;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

/**
 * App MVC Configuration.
 */
@Configuration
@ComponentScan(basePackages = {"org.kitodo.mediaserver.ui", "org.kitodo.mediaserver.core", "org.kitodo.mediaserver.local"})
@EnableWebMvc
@EnableJpaRepositories("org.kitodo.mediaserver.core.db.repositories")
@EntityScan("org.kitodo.mediaserver.core.db.entities")
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // login process is handled by Spring Security so we don't need a separate controller
        registry.addViewController("/login").setViewName("login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Everything in "/static" doesn't need to be processed. Just download these files.
        registry.addResourceHandler("/static/**").addResourceLocations("/static/");
    }

    /**
     * Gets the template resolver which knows where our HTML templates are.
     * @return the template resolver
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setCacheable(false);
        // path to HTML templates
        templateResolver.setPrefix("WEB-INF/views/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        return templateResolver;
    }

    /**
     * Gets the source for messages from language files.
     * @return the message source
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        // path to messages files
        source.setBasename("WEB-INF/locale/messages");
        // the files are UTF-8 encoded
        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return source;
    }

    /**
     * Gets the locale resolver.
     * @return the locale resolver
     */
    @Bean
    public LocaleResolver localeResolver() {

        List<LocaleResolver> localeResolvers = new ArrayList<>();

        // use language cookie or fallback to Accept-Language HTTP header
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(null);
        cookieLocaleResolver.setCookieMaxAge(31536000);
        localeResolvers.add(cookieLocaleResolver);

        // default language
        FixedLocaleResolver fixedLocaleResolver = new FixedLocaleResolver();
        fixedLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        localeResolvers.add(fixedLocaleResolver);

        ChainedLocaleResolver resolver = new ChainedLocaleResolver();
        resolver.setLocaleResolvers(localeResolvers);

        return resolver;
    }

    /**
     * Gets the interceptor for language switching.
     * @return the interceptor
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        // the URL parameter (...?lang=...)
        lci.setParamName("lang");
        return lci;
    }

    /**
     * Adds our locale interceptor.
     * @param registry InterceptorRegistry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    /**
     * Gets the Validator.
     * @return Validator
     */
    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource());
        return validator;
    }

}
