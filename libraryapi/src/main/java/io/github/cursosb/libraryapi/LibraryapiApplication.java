package io.github.cursosb.libraryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//Tive que implementar o reconhecimento dos pacotes manualmente pois estava gerando erro do Mapstruck ao subir a aplicação.
@SpringBootApplication(scanBasePackages = {
		"io.github.cursosb.libraryapi.config",
		"io.github.cursosb.libraryapi.controller",
		"io.github.cursosb.libraryapi.controller.common",
		"io.github.cursosb.libraryapi.controller.dto",
		"io.github.cursosb.libraryapi.controller.mappers",
		"io.github.cursosb.libraryapi.exceptions",
		"io.github.cursosb.libraryapi.model",
		"io.github.cursosb.libraryapi.repository",
		"io.github.cursosb.libraryapi.repository.specs",
		"io.github.cursosb.libraryapi.security",
		"io.github.cursosb.libraryapi.service",
		"io.github.cursosb.libraryapi.validator",
	})
@EnableJpaAuditing
public class LibraryapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryapiApplication.class, args);
	}
}
