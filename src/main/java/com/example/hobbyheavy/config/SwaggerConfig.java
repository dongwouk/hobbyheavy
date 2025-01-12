package com.example.hobbyheavy.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger(OpenAPI 3) 설정 클래스
 * API 문서를 자동으로 생성하고, 보안 스키마를 설정하여 인증 토큰을 처리.
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 객체를 설정하는 Bean
     * API 정보, 보안 스키마 및 기타 구성 요소를 정의.
     *
     * @return OpenAPI 설정 객체
     */
    @Bean
    public OpenAPI openAPI() {
        String customSchemeName = "AccessTokenAuth"; // 보안 스키마의 이름 설정

        // Security Requirement 설정
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(customSchemeName); // 보안 스키마를 요구 사항으로 추가

        // Security Scheme 설정 (API 키 형식으로 "access" 헤더를 사용)
        Components components = new Components()
                .addSecuritySchemes(customSchemeName,
                        new SecurityScheme()
                                .name("access") // 클라이언트가 헤더에 추가할 키 이름 ("access")
                                .type(SecurityScheme.Type.APIKEY) // API Key 방식
                                .in(SecurityScheme.In.HEADER) // 헤더에서 토큰을 찾도록 설정
                                .description("Enter your access token without the 'Bearer ' prefix")); // 설명 추가

        // OpenAPI 설정
        return new OpenAPI()
                .components(components) // 보안 스키마 추가
                .info(new Info()
                        .title("HobbyHeavy API") // API 제목
                        .description("Hobby Sharing Platform API Documentation") // API 설명
                        .version("1.0.0")) // API 버전
                .addSecurityItem(securityRequirement); // 보안 요구 사항 추가
    }
}
