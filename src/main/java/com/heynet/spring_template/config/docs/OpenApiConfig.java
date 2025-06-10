package com.heynet.spring_template.config.docs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {
  @Bean
  OpenAPI openApi() {
    final String securitySchema = "Bearer";
    return new OpenAPI()
        .info(appInfo())
        .addSecurityItem(new SecurityRequirement().addList(securitySchema))
        .components(
            new Components()
                .addSecuritySchemes(
                    securitySchema,
                    new SecurityScheme()
                        .name(securitySchema)
                        .description("Entre com o token JWT obtido no login")
                        .bearerFormat("JWT")
                        .scheme("bearer")
                        .in(SecurityScheme.In.HEADER)
                        .type(SecurityScheme.Type.HTTP)));
  }

  private Info appInfo() {
    return new Info()
        .title("Api Template - Spring Boot")
        .version("0.0.1")
        .description("Template para criação de APIs com Spring Boot e Spring Security");
  }
}
