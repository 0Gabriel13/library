package io.github.cursosb.libraryapi.security;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import io.github.cursosb.libraryapi.service.Clientservice;
import lombok.RequiredArgsConstructor;

/*
 * Essa classe é responsável por buscar informações sobre clientes registrados
 * e fornecer essas informações ao Authorization Server.

   Ela atua como uma ponte entre o Authorization Server e o serviço que
   gerencia os clientes (Clientservice).

   Sem essa classe, o Authorization Server não saberia como validar
   os clientes que tentam se autenticar.

 * A classe não é responsável por todo o Authorization Server,
 * mas sim por fornecer informações sobre clientes registrados para ele.
 * O Authorization Server usa essas informações para tomar decisões durante 
 * o processo de autenticação e autorização.

   A classe não "obtém informações do Authorization Server",
   mas sim fornece informações para o Authorization Server.
 */
@Component
@RequiredArgsConstructor
public class CustomRegisteredClientRepository implements RegisteredClientRepository{

	private final Clientservice clientservice;
	private final TokenSettings tokenSettings;
	private final ClientSettings clientSettings;
	
	@Override
	public void save(RegisteredClient registeredClient) {}

	@Override
	public RegisteredClient findById(String id) {
		return null;
	}

	@Override
	public RegisteredClient findByClientId(String clientId) {
		var client = clientservice.obterPorClientID(clientId);  // Busca o cliente no serviço
		
		if (client == null) { // Cliente não encontrado
			return null;
		}
		
		return RegisteredClient
				.withId(client.getId().toString())
				.clientId(client.getClientId())
				.clientSecret(client.getClientSecret())
				.redirectUri(client.getRedirectURI())
				.scope(client.getScope())
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)// Utilizado para usuario ja cadastrado fornecendo login e senha
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)// Utilizado para logar com outra aplicação . Ex:GOOGLE
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)//Utilizado para restaurar o token de acesso para continuar logado após o tempo expirado
				.tokenSettings(tokenSettings)
				.clientSettings(clientSettings)
				.build();
	}
//	1. Cliente tenta se autenticar
//	   |
//	   +-- 2. Authorization Server recebe a solicitação
//	           |
//	           +-- 3. Chama `CustomRegisteredClientRepository.findByClientId`
//	                   |
//	                   +-- 4. Busca o cliente no `Clientservice`
//	                           |
//	                           +-- 5. Se cliente não existe:
//	                           |       |
//	                           |       +-- Retorna `null` (solicitação rejeitada)
//	                           |
//	                           +-- 6. Se cliente existe:
//	                                   |
//	                                   +-- 7. Constrói um `RegisteredClient`
//	                                           |
//	                                           +-- 8. Retorna para o Authorization Server
//	                                                   |
//	                                                   +-- 9. Authorization Server valida o cliente
//	                                                           |
//	                                                           +-- 10. Se válido:
//	                                                           |       |
//	                                                           |       +-- Emite código/token
//	                                                           |
//	                                                           +-- 11. Se inválido:
//	                                                                   |
//	                                                                   +-- Rejeita a solicitação
}
