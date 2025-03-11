package io.github.cursosb.libraryapi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.cursosb.libraryapi.security.CustomAuthentication;

@Controller
public class LoginViewController {

    @GetMapping("/login")
    public String paginaLogin() {
        return "login"; // Renderiza a página de login
    }
    
    @GetMapping("/")
    public String paginaHome(Authentication authentication, Model model) {
        if (authentication instanceof CustomAuthentication customAuth) {
            // Obtém o login do usuário a partir da autenticação personalizada
            String loginUsuario = customAuth.getUsuario().getLogin(); // Supondo que getUsuario() retorne um objeto Usuario com getLogin()
            model.addAttribute("login", loginUsuario); // Passa o login para a view
            System.out.println("Usuário autenticado: " + loginUsuario);
        } else {
            // Caso a autenticação não seja do tipo CustomAuthentication
            String loginUsuario = authentication.getName(); // Usa o nome padrão do Authentication
            model.addAttribute("login", loginUsuario); // Passa o login para a view
            System.out.println("Usuário autenticado (não custom): " + loginUsuario);
        }
        return "home"; // Renderiza a página home
    }
}