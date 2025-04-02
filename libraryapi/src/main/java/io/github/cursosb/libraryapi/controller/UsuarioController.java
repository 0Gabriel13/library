package io.github.cursosb.libraryapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.github.cursosb.libraryapi.controller.dto.UsuarioDTO;
import io.github.cursosb.libraryapi.controller.mappers.UsuarioMapper;
import io.github.cursosb.libraryapi.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final UsuarioService service;
    private final UsuarioMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GERENTE')")
    public void salvar(@RequestBody @Valid UsuarioDTO dto){
    	
    	log.info("Salvando um novo usuario: {}", dto.login());
    	//Converte um UsuarioDTO em uma entidade Usuario.
        var usuario = mapper.toEntity(dto); 
        service.salvar(usuario);
    }
}
