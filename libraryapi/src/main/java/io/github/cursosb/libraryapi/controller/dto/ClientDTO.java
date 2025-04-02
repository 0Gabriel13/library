package io.github.cursosb.libraryapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
@Schema(name = "Clients")
public record ClientDTO(
		@NotBlank(message = "campo obrigatorio")
		@Schema(name = "Client ID")
		String clientId,
		
		@NotBlank(message = "campo obrigatorio")
		@Schema(name = "Client Secret")
		String clientSecret,
		
		@NotBlank(message = "campo obrigatorio")
		@Schema(name = "Redirect URI")
		String redirectURI,
		
		@NotBlank(message = "campo obrigatorio")
		@Schema(name = "Scope")
		String scope) {

}
