package io.github.cursosb.libraryapi.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import io.github.cursosb.libraryapi.security.CustomAuthentication;

@Configuration
@EnableMethodSecurity
public class AuthorizationServerConfiguration {

	
	//Configuração para habilitar o AuthorizationServer e hablitar e configurar o TokenJWT	
	@Bean
	@Order(1)
	public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
		
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
		
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
		.oidc(Customizer.withDefaults());
		
		http.oauth2ResourceServer(oauth2Rs -> oauth2Rs.jwt(Customizer.withDefaults()));
		
		http.formLogin(configurer -> configurer.loginPage("/login"));
		
		return http.build();
	}
	
    @Bean
    public PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder(10);
    }
    
	@Bean
	public TokenSettings tokenSettings() {
		return TokenSettings.builder()
				.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
				// access_token: token utilizado nas requisiçoes
				.accessTokenTimeToLive(Duration.ofMinutes(60)) //Metodo para definir quanto tempo o token é valido
				// refresh_token: token para renovar o access_token
				.refreshTokenTimeToLive(Duration.ofMinutes(90))
				.build();
	}
	
	@Bean
	public ClientSettings clientSettings() {
		return ClientSettings.builder()
				.requireAuthorizationConsent(false) //Metodo para nao mostrar tela de aceitar autorização
				.build();
	}
	
	//JWK - Json Web key
	@Bean
	public JWKSource<SecurityContext> jwkSource() throws Exception {
		RSAKey rsaKey = gerarChaveRSA();
		JWKSet jwkSet = new JWKSet(rsaKey);
		
		return new ImmutableJWKSet<>(jwkSet);
	}

	//Gerar par de chaves RSA 			
	private RSAKey gerarChaveRSA() throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048); // 2048 Bits
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		
		RSAPublicKey chavePublica =(RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey chavePrivada = (RSAPrivateKey) keyPair.getPrivate();
		
		return new RSAKey
				.Builder(chavePublica)
				.privateKey(chavePrivada)
				.keyID(UUID.randomUUID().toString())
				.build();
	}
	
	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}
	
	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder()
				// URL para obter token
				.tokenEndpoint("/oauth2/token")
				// URL para consultar status do token
				.tokenIntrospectionEndpoint("/oauth2/introspect")
				// URL para revogar o token
				.tokenRevocationEndpoint("/oauth2/revoke")
				// URL para authorization endpoint
				.authorizationEndpoint("/oauth2/authorize")
				// URL sobre informações do usuario OPEN ID CONNECT
				.oidcUserInfoEndpoint("/oauth2/userinfo")
				// URL para obter chave publica pra verificar a assinatura do token
				.jwkSetEndpoint("/oauth2/jwks")
				// URL para logout
				.oidcLogoutEndpoint("/oauth2/logout")
				.build();
	}
	
	
	//O que esse código faz?
	/*
	 * Ele personaliza seu token JWT, adicionando:
	 * Lista de autoridades (roles/permissões)
	 * E-mail do usuário (dado customizado)
	 */
	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(){
		return context -> { // Recebe o contexto do token
		    var principal = context.getPrincipal(); // Pega o usuário autenticado
		    
		    if (principal instanceof CustomAuthentication authentication) { // Verifica se é sua autenticação customizada
		        OAuth2TokenType tipoToken = context.getTokenType(); // Pega o tipo do token (ACCESS ou REFRESH)
		        
		        if (OAuth2TokenType.ACCESS_TOKEN.equals(tipoToken)) { // Só modifica ACCESS TOKEN
		            Collection<GrantedAuthority> authorities = authentication.getAuthorities(); // Pega as permissões
		            
		            List<String> authoritiesList = 
		                authorities.stream().map(GrantedAuthority::getAuthority).toList(); // Transforma em lista
		            
		            // ADICIONA CLAIMS CUSTOMIZADAS AO TOKEN!
		            context.getClaims()
		                .claim("authorities", authoritiesList) // Adiciona roles (ex: ["GERENTE", "USER"])
		                .claim("email", authentication.getUsuario().getEmail()); // Adiciona e-mail
		        }
		    }
		};
	}
}
