package com.heynet.spring_template.config.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CorsConfig {

  @Value("${allowed.origins}")
  private String allowedOrigins;

  @Value("${allowed.methods}")
  private String allowedMethods;

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        log.info("AllowOrigins: {}", allowedOrigins);
        log.info("AllowedMethods: {}", allowedMethods);
        registry
            .addMapping("/**")
            .allowedOrigins(allowedOrigins)
            .allowedMethods(allowedMethods.split(","))
            .allowedHeaders("*");
      }
    };
  }
}
