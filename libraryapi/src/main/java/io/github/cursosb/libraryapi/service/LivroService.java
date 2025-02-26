package io.github.cursosb.libraryapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.cursosb.libraryapi.model.GeneroLivro;
import io.github.cursosb.libraryapi.model.Livro;
import io.github.cursosb.libraryapi.model.Usuario;
import io.github.cursosb.libraryapi.repository.LivroRepository;
import io.github.cursosb.libraryapi.security.SecurityService;
import io.github.cursosb.libraryapi.validator.LivroValidator;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository repository;
    private final LivroValidator validator;
    private final SecurityService securityService;

    public Livro salvar(Livro livro) {
        validator.validar(livro);
        Usuario usuario = securityService.obterUsuarioLogado();
        livro.setUsuario(usuario);
        return repository.save(livro);
    }

    public Optional<Livro> obterPorId(UUID id){
        return repository.findById(id);
    }

    public void deletar(Livro livro){
        repository.delete(livro);
    }

    public Page<Livro> pesquisa(
            String isbn,
            String titulo,
            String nomeAutor,
            GeneroLivro genero,
            Integer anoPublicacao,
            Integer pagina,
            Integer tamanhoPagina){

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);

        if (isbn != null) {
            return repository.findByIsbn(isbn, pageRequest);
        }
        
        if (titulo != null) {
            return repository.findByTituloIgnoreCaseContaining(titulo, pageRequest);
        }
        
        if (genero != null) {
            return repository.findByGenero(genero, pageRequest);
        }
        
        if (anoPublicacao != null) {
            return repository.findByAnoPublicacao(anoPublicacao.toString(), pageRequest);
        }
        
        if (nomeAutor != null) {
            return repository.findByNomeAutorLike(nomeAutor, pageRequest);
        }

        // Se nenhum filtro for aplicado, retorna todos os livros paginados
        return repository.findAll(pageRequest);
    }

    public void atualizar(Livro livro) {
        if(livro.getId() == null){
            throw new IllegalArgumentException("Para atualizar, é necessário que o livro já esteja salvo no banco de dados.");
        }

        validator.validar(livro);
        repository.save(livro);
    }
}
