package com.irku.blog.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI blogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Website Blog API")
                        .description("OpenAPI documentation for Blog and Contact endpoints")
                        .version("v1")
                        .contact(new Contact()
                                .name("Mukesh Joshi")
                                .email("mukesh.bciit@gmail.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project Repository")
                        .url("https://github.com/muku3011"));
    }
}