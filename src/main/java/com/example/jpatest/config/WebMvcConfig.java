package com.example.jpatest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("file:///C:/TravelGenius/event/")
    String eventUploadPath;

    @Value("file:///C:/TravelGenius/item/")
    String itemUploadPath;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/images/**")
                .addResourceLocations(eventUploadPath)
                .addResourceLocations(itemUploadPath);
    }
}
