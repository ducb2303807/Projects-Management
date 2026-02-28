package com.group4.projects_management.core.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
@SecurityScheme(
        name = "bearerAuth", // Tên scheme, đặt gì cũng được
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Nhập JWT Token của bạn vào đây để xác thực"
)
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
