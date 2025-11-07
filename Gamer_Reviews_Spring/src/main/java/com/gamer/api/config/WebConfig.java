package com.gamer.api.config;
//2) Config para servir las imágenes estáticas
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sirve /uploads/** desde la carpeta local 'uploads'
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}