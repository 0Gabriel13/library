package io.github.cursosb.libraryapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import io.github.cursosb.libraryapi.security.JwtCustomAuthenticationFilter;
import io.github.cursosb.libraryapi.security.LoginSocialSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true) //Metodo para funcionar o @PreAuthorize nos controllers
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(
    		HttpSecurity http,
    		LoginSocialSuccessHandler successHandler,
    		JwtCustomAuthenticationFilter jwtCustomAuthenticationFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                //.httpBasic(Customizer.withDefaults())
                //configurar para o spring reonhecer a pagina customizada Login.html 
                  .formLogin(configurer -> {
                    configurer.loginPage("/login").successHandler(successHandler);
                })
//                .formLogin(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> {
                	authorize.requestMatchers("/login/**").permitAll();
                	authorize.requestMatchers(HttpMethod.POST, "/usuarios/**").permitAll();
//                	authorize.requestMatchers(HttpMethod.POST, "/autores/**").hasAuthority("CADASTRAR_AUTOR"); //permite que quem tem essa role de "CADASTRAR_AUTOR" possa cadastrar sem precisar serm ADMIN , ex: GERENTE
//                	authorize.requestMatchers(HttpMethod.POST, "/autores/**").hasRole("ADMIN");
//                	authorize.requestMatchers(HttpMethod.DELETE, "/autores/**").hasRole("ADMIN");
//                	authorize.requestMatchers(HttpMethod.PUT, "/autores/**").hasRole("ADMIN");
//                	authorize.requestMatchers("/livros/**").hasAnyRole("OPERADOR", "GERENTE");//"hasanyrole" permite mais de um
//                	authorize.requestMatchers("/autores/**").hasRole("GERENTE"); //"hasrole" permite apenas 1
                    authorize.anyRequest().authenticated(); // "anyrequests" deve ser o ultimo.
                })
              //configurar para o spring reonhecer a pagina customizada Login.html e a autenticacao via Google
                .oauth2Login(oauth2 -> {
                	oauth2.loginPage("/login")
                	.successHandler(successHandler);
                })
                .oauth2ResourceServer(oauth2RS -> oauth2RS.jwt(Customizer.withDefaults()))
                .addFilterAfter(jwtCustomAuthenticationFilter, BearerTokenAuthenticationFilter.class)
                .build();
    }
   
    
    //Metodo para Eliminar o prefixo ROLE_ do security(CONFIGURA O PREFIXO ROLE)
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
    	return new GrantedAuthorityDefaults("");
    }
    
    
    //CONFIGURA, NO TOKEN JWT, O PREFIXO SCOPE (SCOPE_)
    @Bean
    public JwtAuthenticationConverter authenticationConverter() {
    	var authoritiesConverter = new JwtGrantedAuthoritiesConverter();
    	authoritiesConverter.setAuthorityPrefix("");
    	
    	var converter = new JwtAuthenticationConverter();
    	converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
    	
    	return converter;
    }
    
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
    	return web -> web.ignoring().requestMatchers(
    			"/v2/api-docs/**",
    			"/v3/api-docs/**",
    			"/swagger-resources/**",
    			"/swagger-ui.html",
    			"/swagger-ui/**",
    			"/webjars/**",
    			"/actuator/**"
    			);
    }
}