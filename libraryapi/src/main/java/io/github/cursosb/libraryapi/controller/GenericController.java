package io.github.cursosb.libraryapi.controller;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.UUID;

//
/**
 * Interface que fornece um método padrão para gerar URIs de novos recursos.
 */
public interface GenericController {

    /**
     * Método para gerar a URI de um recurso recém-criado como : Location: http://seuservidor.com/livros/abc123
     */
    default URI gerarHeaderLocation(UUID id) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest() // Obtém a URL da requisição atual
                .path("/{id}") // Adiciona "/{id}" no final da URL
                .buildAndExpand(id) // Substitui "{id}" pelo valor real do UUID
                .toUri(); // Converte para um objeto URI
    }
}
