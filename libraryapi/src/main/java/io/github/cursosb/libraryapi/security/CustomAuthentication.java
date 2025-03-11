package io.github.cursosb.libraryapi.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.github.cursosb.libraryapi.model.Usuario;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

//  Criando nossa propria Authentication ao inves da classe "CustomUserDetailsService"

@RequiredArgsConstructor
@Getter
public class CustomAuthentication implements Authentication{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Usuario usuario;

	/*
	 * Esse método é responsável por retornar as autoridades (roles) do usuário.
	 * Ele faz isso convertendo as roles do usuário em uma lista de GrantedAuthority,
	 *  que é o formato esperado pelo Spring Security para gerenciar permissões.
	 *  O uso de stream() e map() é para transformar as roles em SimpleGrantedAuthority.
	 *  
	 *  O "SimpleGrantedAuthority" é uma classe do Spring Security que implementa
	 *  a interface "GrantedAuthority".
	 *  Ele é usado para representar uma autoridade (ou role) concedida a
	 *  um usuário autenticado.
	 */
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return this.usuario
				.getRoles()
				.stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	/*
	 * Retorna null, o que é comum quando você não
	 *  está lidando com credenciais diretamente
	 *  (como senhas) na autenticação.
	 */
	@Override
	public Object getCredentials() {
		return null;
	}

	/*
	 *  Retorna o objeto Usuario,
	 *  o que pode ser útil para acessar informações 
	 *  adicionais sobre o usuário autenticado.
	 */
	@Override
	public Object getDetails() {
		return usuario;
	}

	/*
	 * Retorna o objeto Usuario,
	 * que é o principal (identidade) do usuário autenticado.
	 */
	@Override
	public Object getPrincipal() {
		return usuario;
	}

	// sempre retorna true, assumindo que a autenticação já ocorreu com sucesso.
	@Override
	public boolean isAuthenticated() {
		return true;
	}

	//Esse método é vazio, o que é comum em implementações de Authentication
	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
	}
	
	//Retorna o login do usuário, que é o identificador único do usuário.
	@Override
	public String getName() {
		return usuario.getLogin();
	}

}
