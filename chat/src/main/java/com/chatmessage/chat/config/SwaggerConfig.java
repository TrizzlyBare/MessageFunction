package com.chatmessage.chat.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.FileSchema;
import io.swagger.v3.oas.models.parameters.Parameter;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Chat API")
                        .version("1.0.0")
                        .description("API for chat messaging application - Prototype version")
                        .contact(new Contact()
                                .name("Chat Team")
                                .email("contact@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0")));
    }
    
    @Bean
    public OpenApiCustomizer consumerTypeCustomizer() {
        return openApi -> {
            // Define file schema for upload endpoints
            Components components = openApi.getComponents();
            if (components == null) {
                components = new Components();
                openApi.components(components);
            }
            
            // Add a schema for file uploads
            Schema<?> fileSchema = new FileSchema();
            components.addSchemas("FileUpload", fileSchema);
        };
    }
}
