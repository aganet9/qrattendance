package ru.chsu.qrattendance.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${keycloak.url:http://localhost:8081}")
    private String keycloakUrl;

    @Bean
    public OpenAPI openAPI() {
        String authUrl = keycloakUrl + "/realms/UniversityAttendance/protocol/openid-connect/auth";
        String tokenUrl = keycloakUrl + "/realms/UniversityAttendance/protocol/openid-connect/token";

        return new OpenAPI()
                .info(new Info()
                        .title("QR Attendance API")
                        .version("0.1")
                        .description("API для учета посещаемости по QR-коду"))
                .addSecurityItem(new SecurityRequirement().addList("Keycloak"))
                .schemaRequirement("Keycloak",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl(authUrl)
                                                .tokenUrl(tokenUrl)
                                        )
                                )
                );
    }
}
