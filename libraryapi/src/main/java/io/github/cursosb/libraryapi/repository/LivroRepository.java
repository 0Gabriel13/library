package io.github.cursosb.libraryapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.cursosb.libraryapi.model.Autor;
import io.github.cursosb.libraryapi.model.GeneroLivro;
import io.github.cursosb.libraryapi.model.Livro;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LivroRepository extends JpaRepository<Livro, UUID> {
	
	boolean existsByAutor(Autor autor);

    // Pesquisa por ISBN
    Optional<Livro> findByIsbn(String isbn);
    Page<Livro> findByIsbn(String isbn, Pageable pageable);

    // Pesquisa por título (ignorando maiúsculas e minúsculas)
    List<Livro> findByTituloIgnoreCaseContaining(String titulo);
    Page<Livro> findByTituloIgnoreCaseContaining(String titulo, Pageable pageable);

    // Pesquisa por gênero
    List<Livro> findByGenero(GeneroLivro genero);
    Page<Livro> findByGenero(GeneroLivro genero, Pageable pageable);

    // Pesquisa por ano de publicação (convertendo para string)
    @Query("SELECT l FROM Livro l WHERE FUNCTION('to_char', l.dataPublicacao, 'YYYY') = :ano")
    List<Livro> findByAnoPublicacao(@Param("ano") String anoPublicacao);
    
    @Query("SELECT l FROM Livro l WHERE FUNCTION('to_char', l.dataPublicacao, 'YYYY') = :ano")
    Page<Livro> findByAnoPublicacao(@Param("ano") String anoPublicacao, Pageable pageable);

    // Pesquisa por nome do autor
    @Query("SELECT l FROM Livro l JOIN l.autor a WHERE UPPER(a.nome) LIKE UPPER(CONCAT('%', :nome, '%'))")
    List<Livro> findByNomeAutorLike(@Param("nome") String nomeAutor);
    
    @Query("SELECT l FROM Livro l JOIN l.autor a WHERE UPPER(a.nome) LIKE UPPER(CONCAT('%', :nome, '%'))")
    Page<Livro> findByNomeAutorLike(@Param("nome") String nomeAutor, Pageable pageable);
}
