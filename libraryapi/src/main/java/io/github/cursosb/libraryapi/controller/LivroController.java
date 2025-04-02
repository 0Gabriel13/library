package io.github.cursosb.libraryapi.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.cursosb.libraryapi.controller.dto.CadastroLivroDTO;
import io.github.cursosb.libraryapi.controller.dto.ResultadoPesquisaLivroDTO;
import io.github.cursosb.libraryapi.controller.mappers.LivroMapper;
import io.github.cursosb.libraryapi.model.GeneroLivro;
import io.github.cursosb.libraryapi.model.Livro;
import io.github.cursosb.libraryapi.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
@Tag(name = "Livros")
public class LivroController implements GenericController {

    private final LivroService service;
    private final LivroMapper mapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @Operation(summary = "Salvar", description = "Cadastrar um livro")
    @ApiResponses({
    	@ApiResponse(responseCode = "200", description = "Livro criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Sintaxe da requisição inválida"),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "422", description = """
            Erro de validação semântica:
            - Data de publicação futura
            - Preço faltando para livros a partir de 2020
            - Formato de ISBN inválido
            """),
        @ApiResponse(responseCode = "409", description = "ISBN já cadastrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "500", description = "Ocorreu um erro inesperado. Entre em contato com a administração.")
    })
    public ResponseEntity<Void> salvar(@RequestBody @Valid CadastroLivroDTO dto) {
        Livro livro = mapper.toEntity(dto);
        service.salvar(livro);
        var url = gerarHeaderLocation(livro.getId());
        return ResponseEntity.created(url).build();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @Operation(summary = "Consultar", description = "Consultar um livro por ID")
    @ApiResponses({
    	    @ApiResponse(responseCode = "200", description = "Livro encontrado."),
    	    @ApiResponse(responseCode = "403", description = "Acesso negado."),
    	    @ApiResponse(responseCode = "401", description = "Não autenticado"),
    	    @ApiResponse(responseCode = "404", description = "Livro não encontrado."),
    	    @ApiResponse(responseCode = "422", description = "ID em formato UUID inválido."),
    	    @ApiResponse(responseCode = "500", description = "Ocorreu um erro inesperado. Entre em contato com a administração.")
    	})
    public ResponseEntity<ResultadoPesquisaLivroDTO> obterDetalhes(
            @PathVariable("id") String id){
        return service.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    var dto = mapper.toDTO(livro);
                    return ResponseEntity.ok(dto);
                }).orElseGet( () -> ResponseEntity.notFound().build() );
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @Operation(summary = "Deletar", description = "Deletar um livro por ID")
    @ApiResponses(value = {
    	    @ApiResponse(responseCode = "204", description = "Livro excluído com sucesso."),
    	    @ApiResponse(responseCode = "403", description = "Acesso negado."),
    	    @ApiResponse(responseCode = "401", description = "Não autenticado"),
    	    @ApiResponse(responseCode = "404", description = "Livro não encontrado."),
    	    @ApiResponse(responseCode = "422", description = "ID em formato inválido."),
    	    @ApiResponse(responseCode = "500", description = "Ocorreu um erro inesperado. Entre em contato com a administração.")
    	})
    public ResponseEntity<Object> deletar(@PathVariable("id") String id){
        return service.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    service.deletar(livro);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @Operation(summary = "Busca Paginada", description = "Bucar livros de forma paginada.")
    @ApiResponses({
    	    @ApiResponse(responseCode = "200", description = "Lista de autores retornada com sucesso"),
    	    @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos"),
    	    @ApiResponse(responseCode = "401", description = "Não autenticado"),
    	    @ApiResponse(responseCode = "422", description = "Filtros em formato inválido (datas, valores incorretos)"),
    	    @ApiResponse(responseCode = "401", description = "Acesso não autorizado"),
    	    @ApiResponse(responseCode = "500", description = "Ocorreu um erro inesperado. Entre em contato com a administração.")
    	})
    public ResponseEntity<Page<ResultadoPesquisaLivroDTO>> pesquisa(
            @RequestParam(value = "isbn", required = false)
            String isbn,
            @RequestParam(value = "titulo", required = false)
            String titulo,
            @RequestParam(value = "nome-autor", required = false)
            String nomeAutor,
            @RequestParam(value = "genero", required = false)
            GeneroLivro genero,
            @RequestParam(value = "ano-publicacao", required = false)
            Integer anoPublicacao,
            @RequestParam(value = "pagina", defaultValue = "0")
            Integer pagina,
            @RequestParam(value = "tamanho-pagina", defaultValue = "10")
            Integer tamanhoPagina
    ){
        Page<Livro> paginaResultado = service.pesquisa(
                isbn, titulo, nomeAutor, genero, anoPublicacao, pagina, tamanhoPagina);

        Page<ResultadoPesquisaLivroDTO> resultado = paginaResultado.map(mapper::toDTO);

        return ResponseEntity.ok(resultado);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @Operation(summary = "Atualizar", description = "Atualizar um livro.")
    @ApiResponses({
    	    @ApiResponse(responseCode = "200", description = "Livro atualizado"),
    	    @ApiResponse(responseCode = "400", description = "Sintaxe inválida"),
    	    @ApiResponse(responseCode = "401", description = "Não autenticado"),
    	    @ApiResponse(responseCode = "422", description = """
    	        Erro de validação:
    	        - Data futura
    	        - Preço obrigatório pós-2020
    	        - Autor inexistente
    	        """),
    	    @ApiResponse(responseCode = "409", description = "ISBN duplicado em outra obra."),
    	    @ApiResponse(responseCode = "404", description = "Livro não encontrado."),
    	    @ApiResponse(responseCode = "500", description = "Ocorreu um erro inesperado. Entre em contato com a administração.")
    	})
    public ResponseEntity<Object> atualizar(
            @PathVariable("id") String id, @RequestBody @Valid CadastroLivroDTO dto){
        return service.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    Livro entidadeAux = mapper.toEntity(dto);

                    livro.setDataPublicacao(entidadeAux.getDataPublicacao());
                    livro.setIsbn(entidadeAux.getIsbn());
                    livro.setPreco(entidadeAux.getPreco());
                    livro.setGenero(entidadeAux.getGenero());
                    livro.setTitulo(entidadeAux.getTitulo());
                    livro.setAutor(entidadeAux.getAutor());

                    service.atualizar(livro);

                    return ResponseEntity.noContent().build();

                }).orElseGet( () -> ResponseEntity.notFound().build() );
    }

}
