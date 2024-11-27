package com.example.hobbyheavy.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String customSchemeName = "AccessTokenAuth";

        // Security Requirement 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(customSchemeName);

        // Security Scheme 설정 (Bearer 없이 access라는 키로 설정)
        Components components = new Components().addSecuritySchemes(customSchemeName,
                new SecurityScheme()
                        .name("access") // 헤더 키 이름을 "access"로 설정
                        .type(SecurityScheme.Type.APIKEY) // API Key 형식
                        .in(SecurityScheme.In.HEADER) // 헤더에 위치
                        .description("Enter your access token without the 'Bearer ' prefix"));

        return new OpenAPI()
                .components(components)
                .info(new Info().title("HobbyHeavy API")
                        .description("Hobby Sharing Platform API Documentation")
                        .version("1.0.0"))
                .addSecurityItem(securityRequirement);
    }
}
