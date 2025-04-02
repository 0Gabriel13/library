package io.github.cursosb.libraryapi.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "Usuario")
public record UsuarioDTO(
        @NotBlank(message = "campo obrigatorio")
        @Schema(name = "Login")
        String login,
        
        @Email (message = "inválido")
        @NotBlank(message = "campo obrigatorio")
        @Schema(name = "Email")
        String email,
        
        @NotBlank(message = "campo obrigatorio")
        @Schema(name = "Senha")
        String senha,
        
        @Schema(name = "Roles")
        List<String> roles) {
}
