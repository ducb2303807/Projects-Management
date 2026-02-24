package com.group4.projects_management.core.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("All Endpoints")
                .pathsToMatch("/api/**")
                .addOpenApiCustomizer(openApi -> openApi.getPaths().values().forEach(pathItem ->
                        pathItem.readOperations().forEach(operation ->
                                operation.setTags(Collections.singletonList("Danh sách API"))
                        )
                ))
                .build();
    }
}
