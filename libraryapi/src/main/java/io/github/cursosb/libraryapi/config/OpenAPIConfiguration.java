package io.github.cursosb.libraryapi.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(
		info = @Info(
				title = "Library API",
				version = "version 1.0",
				contact = @Contact(
						name = "Gabriel Oliveira",
						email = "gabrieloliveira@gmail.com",
						url = "library.com"
						)
				),
		security = {
				@SecurityRequirement(name = "bearerAuth")
		}
)

@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		scheme = "bearer",
		in = SecuritySchemeIn.HEADER
		)
public class OpenAPIConfiguration {

}
