package org.wespeak.template.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Value("${app.name}")
  private String appName;

  @Value("${app.version}")
  private String appVersion;

  @Value("${app.description}")
  private String appDescription;

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title(appName)
                .version(appVersion)
                .description(appDescription)
                .contact(
                    new Contact()
                        .name("WeSpeak Team")
                        .email("tech@wespeak.org")
                        .url("https://github.com/we-speak-org"))
                .license(
                    new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
        .components(
            new Components()
                .addSecuritySchemes(
                    "bearer-jwt",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token from Keycloak")))
        .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
  }
}
