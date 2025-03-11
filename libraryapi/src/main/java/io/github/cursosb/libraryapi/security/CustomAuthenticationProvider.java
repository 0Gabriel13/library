package io.github.cursosb.libraryapi.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.github.cursosb.libraryapi.model.Usuario;
import io.github.cursosb.libraryapi.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

//Classe para customizar autenticacao e reeconhecimento via usuario e senha no LoginViewController
public class CustomAuthenticationProvider implements AuthenticationProvider{
	
	private final UsuarioService usuarioService;
	private final PasswordEncoder encoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String login = authentication.getName();
		String senhaDigitada = authentication.getCredentials().toString(); //toString por que é um  objeto, pode ser senha, faceid, digital , etc....
		
		Usuario usuarioEncontrado = usuarioService.obterPorLogin(login);
		
		if (usuarioEncontrado == null) {
			throw getErroUsuarioNaoEncontrado();
		}
		
		
		String senhaCriptografada = usuarioEncontrado.getSenha();
		
		
		//recebendo a senha fornecida e decodificando para ver se batem
		boolean senhasBatem = encoder.matches(senhaDigitada, senhaCriptografada);
		
		if (senhasBatem) {
			return new CustomAuthentication(usuarioEncontrado);
		}
		
	    throw getErroUsuarioNaoEncontrado();
		
	}
	
	private AuthenticationException getErroUsuarioNaoEncontrado() {
	    return new BadCredentialsException("Usuário e/ou senha incorretos!");
	}
	
	
	//metodo para dizer quais tipos de authentication que suporta o AuthenticationProvider
	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
	}

}
