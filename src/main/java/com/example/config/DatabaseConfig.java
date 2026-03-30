package com.example.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@Profile("!test")
public class DatabaseConfig {

	@Bean
	DataSource dataSource(Environment environment) {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setJdbcUrl(resolveJdbcUrl(environment));
		dataSource.setUsername(resolveUsername(environment));
		dataSource.setPassword(resolvePassword(environment));
		return dataSource;
	}

	private String resolveJdbcUrl(Environment environment) {
		String configuredJdbcUrl = firstNonBlank(environment,
				"SPRING_DATASOURCE_URL",
				"JDBC_DATABASE_URL");
		if (configuredJdbcUrl != null) {
			return configuredJdbcUrl;
		}

		String rawDatabaseUrl = firstNonBlank(environment,
				"DATABASE_URL",
				"POSTGRES_URL",
				"DATABASE_PUBLIC_URL",
				"DATABASE_PRIVATE_URL");
		if (rawDatabaseUrl != null) {
			return toJdbcUrl(rawDatabaseUrl);
		}

		String host = firstNonBlank(environment, "PGHOST", "POSTGRES_HOST");
		String port = firstNonBlank(environment, "PGPORT", "POSTGRES_PORT");
		String database = firstNonBlank(environment, "PGDATABASE", "POSTGRES_DB");
		if (host != null && port != null && database != null) {
			return "jdbc:postgresql://" + host + ":" + port + "/" + database;
		}

		throw new IllegalStateException(
				"Database configuration missing. Set SPRING_DATASOURCE_URL, JDBC_DATABASE_URL, DATABASE_URL, or PGHOST/PGPORT/PGDATABASE.");
	}

	private String resolveUsername(Environment environment) {
		String configuredUsername = firstNonBlank(environment,
				"SPRING_DATASOURCE_USERNAME",
				"JDBC_DATABASE_USERNAME",
				"DATABASE_USERNAME",
				"PGUSER",
				"POSTGRES_USER");
		if (configuredUsername != null) {
			return configuredUsername;
		}

		Credentials credentials = credentialsFromUrl(environment);
		return credentials.username();
	}

	private String resolvePassword(Environment environment) {
		String configuredPassword = firstNonBlank(environment,
				"SPRING_DATASOURCE_PASSWORD",
				"JDBC_DATABASE_PASSWORD",
				"DATABASE_PASSWORD",
				"PGPASSWORD",
				"POSTGRES_PASSWORD");
		if (configuredPassword != null) {
			return configuredPassword;
		}

		Credentials credentials = credentialsFromUrl(environment);
		return credentials.password();
	}

	private Credentials credentialsFromUrl(Environment environment) {
		String rawDatabaseUrl = firstNonBlank(environment,
				"DATABASE_URL",
				"POSTGRES_URL",
				"DATABASE_PUBLIC_URL",
				"DATABASE_PRIVATE_URL");
		if (rawDatabaseUrl == null || rawDatabaseUrl.startsWith("jdbc:")) {
			return new Credentials("postgres", "");
		}

		try {
			URI uri = new URI(rawDatabaseUrl);
			String userInfo = uri.getUserInfo();
			if (userInfo == null || userInfo.isBlank()) {
				return new Credentials("postgres", "");
			}

			String[] parts = userInfo.split(":", 2);
			String username = parts.length > 0 ? parts[0] : "postgres";
			String password = parts.length > 1 ? parts[1] : "";
			return new Credentials(username, password);
		} catch (URISyntaxException exception) {
			throw new IllegalStateException("Invalid DATABASE_URL format", exception);
		}
	}

	private String toJdbcUrl(String databaseUrl) {
		if (databaseUrl.startsWith("jdbc:")) {
			return databaseUrl;
		}

		try {
			URI uri = new URI(databaseUrl);
			String scheme = Objects.requireNonNullElse(uri.getScheme(), "");
			if (!"postgres".equalsIgnoreCase(scheme) && !"postgresql".equalsIgnoreCase(scheme)) {
				throw new IllegalStateException("Unsupported database URL scheme: " + databaseUrl);
			}

			StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
					.append(uri.getHost());
			if (uri.getPort() != -1) {
				jdbcUrl.append(':').append(uri.getPort());
			}
			jdbcUrl.append(uri.getPath());
			if (uri.getQuery() != null && !uri.getQuery().isBlank()) {
				jdbcUrl.append('?').append(uri.getQuery());
			}
			return jdbcUrl.toString();
		} catch (URISyntaxException exception) {
			throw new IllegalStateException("Invalid DATABASE_URL format", exception);
		}
	}

	private String firstNonBlank(Environment environment, String... keys) {
		for (String key : keys) {
			String value = environment.getProperty(key);
			if (value != null && !value.isBlank()) {
				return value;
			}
		}
		return null;
	}

	private record Credentials(String username, String password) {
	}
}
