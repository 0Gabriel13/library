package io.github.cursosb.libraryapi.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.ISBN;

import io.github.cursosb.libraryapi.model.GeneroLivro;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
@Schema(name = "Cadastro Livro")
public record CadastroLivroDTO(
        @ISBN
        @NotBlank(message = "campo obrigatorio")
        @Schema(name = "ISBN")
        String isbn,
        
        @NotBlank(message = "campo obrigatorio")
        @Schema(name = "Titulo")
        String titulo,
        
        @NotNull(message = "campo obrigatorio")
        @Past(message = "nao pode ser uma data futura")
        @Schema(name = "Data De Publicacao")
        LocalDate dataPublicacao,
        
        @Schema(name = "Genero")
        GeneroLivro genero,
        
        @Schema(name = "Preco")
        BigDecimal preco,
        
        @Schema(name = "Id Autor")
        @NotNull(message = "campo obrigatorio")
        UUID idAutor
        ) {
}
