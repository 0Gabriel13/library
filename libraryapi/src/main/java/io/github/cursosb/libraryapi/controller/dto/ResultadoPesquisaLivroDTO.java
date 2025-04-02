package io.github.cursosb.libraryapi.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import io.github.cursosb.libraryapi.model.GeneroLivro;
import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "Resultado de pesquisa")
public record ResultadoPesquisaLivroDTO(
        UUID id,
        String isbn,
        String titulo,
        LocalDate dataPublicacao,
        GeneroLivro genero,
        BigDecimal preco,
        AutorDTO autor
) {
}
