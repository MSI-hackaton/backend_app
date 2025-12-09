package dev.msi_hackaton.backend_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${storage.local.directory:./uploads}")
    private String storageDirectory;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Маппинг для доступа к загруженным файлам
        registry.addResourceHandler("/storage/**")
                .addResourceLocations("file:" + storageDirectory + "/");
    }
}