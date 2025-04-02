package io.github.cursosb.libraryapi.controller.mappers;

import org.mapstruct.Mapper;

import io.github.cursosb.libraryapi.controller.dto.ClientDTO;
import io.github.cursosb.libraryapi.model.Client;

@Mapper(componentModel = "spring")
public interface ClientMapper {

	Client toEntity(ClientDTO dto);
}
