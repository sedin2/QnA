package com.sedin.qna.common.configuration;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfigInterceptors implements WebMvcConfigurer {

//    private final AuthenticationInterceptor authenticationInterceptor;
//
//    public WebConfigInterceptors(AuthenticationInterceptor authenticationInterceptor) {
//        this.authenticationInterceptor = authenticationInterceptor;
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(authenticationInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/favicon.ico", "/api/docs/**", "/api/auth/**", "/static/**")
//                .order(Ordered.LOWEST_PRECEDENCE);
//    }
}
