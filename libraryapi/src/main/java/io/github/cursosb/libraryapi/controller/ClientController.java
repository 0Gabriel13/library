package io.github.cursosb.libraryapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.cursosb.libraryapi.controller.dto.ClientDTO;
import io.github.cursosb.libraryapi.controller.mappers.ClientMapper;
import io.github.cursosb.libraryapi.service.Clientservice;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {

	private final Clientservice service;
	private final ClientMapper mapper;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('GERENTE')")
	public void salvar(@RequestBody @Valid ClientDTO dto) {
		
		log.info("Registrando um novo Client: {}, com scope: {}" , dto.clientId(), dto.scope());
		//Converte um ClientDTO em uma entidade Client.
		var client = mapper.toEntity(dto);
		service.salvar(client);
	}
	
}
