package io.github.cursosb.libraryapi.controller.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.cursosb.libraryapi.controller.dto.CadastroLivroDTO;
import io.github.cursosb.libraryapi.controller.dto.ResultadoPesquisaLivroDTO;
import io.github.cursosb.libraryapi.model.Livro;
import io.github.cursosb.libraryapi.repository.AutorRepository;

@Mapper(componentModel = "spring", uses = AutorMapper.class )
public abstract class LivroMapper {

    @Autowired
    AutorRepository autorRepository;

    @Mapping(target = "autor", expression = "java( autorRepository.findById(dto.idAutor()).orElse(null) )")
    public abstract Livro toEntity(CadastroLivroDTO dto);

    public abstract ResultadoPesquisaLivroDTO toDTO(Livro livro);
}
