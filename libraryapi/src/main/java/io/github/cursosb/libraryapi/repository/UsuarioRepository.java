package io.github.cursosb.libraryapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.cursosb.libraryapi.model.Usuario;

import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Usuario findByLogin(String login);

    Usuario findByEmail(String email);
    
    
}
