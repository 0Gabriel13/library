package io.github.cursosb.libraryapi.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import io.github.cursosb.libraryapi.model.Usuario;
import io.github.cursosb.libraryapi.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
//Classe para customizar autenticacao e reeconhecimento via Google no LoginViewController

public class LoginSocialSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	//Definindo senha padrao ao se cadastrar pelo google usando apenas o email
	private final static String SENHA_PADRAO = "123";
	
	private final UsuarioService usuarioService;

	@Override
	public void onAuthenticationSuccess(
	        HttpServletRequest request,
	        HttpServletResponse response,
	        Authentication authentication) throws ServletException, IOException {

	    // Verifica se a autenticação é do tipo OAuth2 (Google)
	    if (authentication instanceof OAuth2AuthenticationToken) {
	        // Faz o cast do objeto Authentication para OAuth2AuthenticationToken
	        OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
	        
	        // Obtém o principal (usuário autenticado) do token OAuth2
	        OAuth2User oAuth2User = auth2AuthenticationToken.getPrincipal();

	        // Obtém o email do usuário autenticado via Google
	        String email = oAuth2User.getAttribute("email");

	        // Busca o usuário no banco de dados pelo email
	        Usuario usuario = usuarioService.obterPorEmail(email);
	        
	        if (usuario == null) {
				usuario = CadastrarUsuarioNaBase(email);
			}

	        // Cria uma nova autenticação personalizada (CustomAuthentication) com o usuário encontrado
	        authentication = new CustomAuthentication(usuario);
	        
	        // Atualiza o contexto de segurança com a nova autenticação
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	        
	    }

	    // Chama o comportamento padrão do Spring Security para finalizar o processo de autenticação
	    super.onAuthenticationSuccess(request, response, authentication);
	}
	
	//Cadastrar usuario no banco ao logar pelo email
	private Usuario CadastrarUsuarioNaBase(String email) {
		Usuario usuario;
		usuario = new Usuario();
		usuario.setEmail(email);
		
		usuario.setLogin(ObterLoginApartirEmail(email));
		
		usuario.setSenha(SENHA_PADRAO);
		usuario.setRoles(List.of("OPERADOR"));
		
		usuarioService.salvar(usuario);
		return usuario;
		
	}
	
	/*
	 * NOTA: da posição "0" até o indice do "@", ou seja: se o email for
	 * "pessoa@gmail.com", ele vai pegar da posiçao "0" da String (inicio da palavra pessoa)
	 * até a letra "a" que vem antes do "@" (no substring o segundo index é exclusivo.
	 */
	private String ObterLoginApartirEmail(String email) {
		return email.substring(0, email.indexOf("@"));
	}
}
