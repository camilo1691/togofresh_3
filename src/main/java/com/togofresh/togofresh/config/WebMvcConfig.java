package com.togofresh.togofresh.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Configuración de CORS para desarrollo
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:4200") // Angular u otro frontend
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .exposedHeaders("Authorization") // Para JWT
            .allowCredentials(true)
            .maxAge(3600);
    }

    /**
     * Mapeo de vistas estáticas (Thymeleaf)
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Autenticación
        registry.addViewController("/login").setViewName("auth/login");
        registry.addViewController("/recuperar-contrasena").setViewName("auth/recuperar-contrasena");
        
        // Módulos
        registry.addViewController("/ventas").setViewName("modules/ventas");
        registry.addViewController("/inventario").setViewName("modules/inventario");
        
        // Admin
        registry.addViewController("/admin/usuarios").setViewName("admin/usuarios");
        registry.addViewController("/admin/roles").setViewName("admin/roles");
    }

    /**
     * Manejo de recursos estáticos (CSS, JS, imágenes)
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(3600);
        
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:uploads/");
    }

    /**
     * Conversores personalizados para tipos de datos
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // Ejemplo para convertir String a LocalDateTime
        // registry.addConverter(new StringToLocalDateTimeConverter());
    }
}