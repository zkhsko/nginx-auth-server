package org.nginx.auth.configuration;

import org.nginx.auth.interceptor.AdminSessionInterceptor;
import org.nginx.auth.interceptor.UserSessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private AdminSessionInterceptor adminSessionInterceptor;
    @Autowired
    private UserSessionInterceptor userSessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminSessionInterceptor)
                .addPathPatterns("/admin/**");
        registry.addInterceptor(userSessionInterceptor)
                .addPathPatterns("/**");
    }
}

