package com.example.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	private final List<String> allowedOrigins;
	private final List<String> allowedOriginPatterns;

	public CorsConfig(
			@Value("${app.cors.allowed-origins:http://127.0.0.1:5500,http://localhost:5500}") String allowedOrigins,
			@Value("${app.cors.allowed-origin-patterns:https://*.vercel.app}") String allowedOriginPatterns) {
		this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
				.map(String::trim)
				.filter(origin -> !origin.isEmpty())
				.collect(Collectors.toList());
		this.allowedOriginPatterns = Arrays.stream(allowedOriginPatterns.split(","))
				.map(String::trim)
				.filter(origin -> !origin.isEmpty())
				.collect(Collectors.toList());
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOrigins(allowedOrigins.toArray(String[]::new))
				.allowedOriginPatterns(allowedOriginPatterns.toArray(String[]::new))
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.maxAge(3600);
	}
}
