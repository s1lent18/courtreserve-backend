package com.example.courtreserve;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi customApi() {
        return GroupedOpenApi.builder()
                .group("custom")
                .pathsToMatch("/**")
                .build();
    }
}

