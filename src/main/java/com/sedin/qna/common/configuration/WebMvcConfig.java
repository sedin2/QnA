package com.sedin.qna.common.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

//    private final AuthenticationInterceptor authenticationInterceptor;
//
//    public WebMvcConfig(AuthenticationInterceptor authenticationInterceptor) {
//        this.authenticationInterceptor = authenticationInterceptor;
//    }
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/api/docs/**")
//                .addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/docs/");
//    }
//
//    @Bean
//    public WebMvcConfigurer webMvcConfigurerInterceptors() {
//        return new WebConfigInterceptors(authenticationInterceptor);
//    }
}
