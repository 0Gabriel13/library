package io.github.cursosb.libraryapi.controller.mappers;

import org.mapstruct.Mapper;

import io.github.cursosb.libraryapi.controller.dto.UsuarioDTO;
import io.github.cursosb.libraryapi.model.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

	// Converte DTO para Entidade
    Usuario toEntity(UsuarioDTO dto);
}
