package io.github.cursosb.libraryapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import io.github.cursosb.libraryapi.model.GeneroLivro;
import io.github.cursosb.libraryapi.model.Livro;
import io.github.cursosb.libraryapi.model.Usuario;
import io.github.cursosb.libraryapi.repository.LivroRepository;
import io.github.cursosb.libraryapi.security.SecurityService;
import io.github.cursosb.libraryapi.validator.LivroValidator;

import static io.github.cursosb.libraryapi.repository.specs.LivroSpecs.*;

import java.util.List;
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

     //isbn, titulo, nome autor, genero, ano de publicação
    public Page<Livro> pesquisa(
            String isbn,
            String titulo,
            String nomeAutor,
            GeneroLivro genero,
            Integer anoPublicacao,
            Integer pagina,
            Integer tamanhoPagina){

        // select * from livro where isbn = :isbn and nomeAutor =

//        Specification<Livro> specs = Specification
//                .where(LivroSpecs.isbnEqual(isbn))
//                .and(LivroSpecs.tituloLike(titulo))
//                .and(LivroSpecs.generoEqual(genero))
//                ;

        // select * from livro where 0 = 0
        Specification<Livro> specs = Specification.where((root, query, cb) -> cb.conjunction() );

        if(isbn != null){
            // query = query and isbn = :isbn
            specs = specs.and(isbnEqual(isbn));
        }

        if(titulo != null){
            specs = specs.and(tituloLike(titulo));
        }

        if(genero != null){
            specs = specs.and(generoEqual(genero));
        }

        if(anoPublicacao != null){
            specs = specs.and(anoPublicacaoEqual(anoPublicacao));
        }

        if(nomeAutor != null){
            specs = specs.and(nomeAutorLike(nomeAutor));
        }

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);

        return repository.findAll(specs, pageRequest);
    }

    public void atualizar(Livro livro) {
        if(livro.getId() == null){
            throw new IllegalArgumentException("Para atualizar, é necessário que o livro já esteja salvo no banco de dados.");
        }

        validator.validar(livro);
        repository.save(livro);
    }
}
