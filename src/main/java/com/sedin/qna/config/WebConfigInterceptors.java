package com.sedin.qna.config;

import com.sedin.qna.interceptor.AuthenticationInterceptor;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfigInterceptors implements WebMvcConfigurer {

    private final OpenEntityManagerInViewInterceptor openEntityManagerInViewInterceptor;
    private final AuthenticationInterceptor authenticationInterceptor;

    public WebConfigInterceptors(OpenEntityManagerInViewInterceptor openEntityManagerInViewInterceptor,
                                 AuthenticationInterceptor authenticationInterceptor) {
        this.openEntityManagerInViewInterceptor = openEntityManagerInViewInterceptor;
        this.authenticationInterceptor = authenticationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addWebRequestInterceptor(openEntityManagerInViewInterceptor)
                .addPathPatterns("/**")
                .order(0);
        registry.addInterceptor(authenticationInterceptor)
                .excludePathPatterns("/favicon.ico", "/api/docs/*", "/api/auth/*");
    }
}
