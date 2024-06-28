package com.example.jpatest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final String EVENT_UPLOAD_PATH = "/home/ubuntu/event/";
    private static final String ITEM_UPLOAD_PATH = "/home/ubuntu/item/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/images/**")
//                .addResourceLocations("file:" + EVENT_UPLOAD_PATH);

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + ITEM_UPLOAD_PATH);
    }
}
