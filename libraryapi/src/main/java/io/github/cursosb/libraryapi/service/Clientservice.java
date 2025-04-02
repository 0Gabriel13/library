package io.github.cursosb.libraryapi.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.cursosb.libraryapi.model.Client;
import io.github.cursosb.libraryapi.repository.ClientRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Clientservice {

	private final ClientRepository repository;
	private final PasswordEncoder encoder;
	
	public Client salvar(Client client) {
		var senhaCriptografada = encoder.encode(client.getClientSecret());
		client.setClientSecret(senhaCriptografada);
		return repository.save(client);
	}
	
	public Client obterPorClientID(String clientId) {
		return repository.findByClientId(clientId);
	}
}
