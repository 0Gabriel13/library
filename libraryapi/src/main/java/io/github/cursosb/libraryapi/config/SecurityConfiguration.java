package io.github.cursosb.libraryapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import io.github.cursosb.libraryapi.security.LoginSocialSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true) //Metodo para funcionar o @PreAuthorize nos controllers
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(
    		HttpSecurity http, LoginSocialSuccessHandler successHandler ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
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
                .build();
    }
    
//  @Bean
//    public UserDetailsService userDetailsService(UsuarioService usuarioService) {
//        return new CustomUserDetailsService(usuarioService);
//    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder(10);
    }
    
    //Metodo Eliminar o prefixo ROLE do security
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
    	return new GrantedAuthorityDefaults("");
    }
}