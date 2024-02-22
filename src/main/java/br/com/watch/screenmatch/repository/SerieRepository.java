package br.com.watch.screenmatch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.watch.screenmatch.model.Serie;

public interface SerieRepository extends JpaRepository<Serie, Long> {
	Optional<Serie>findByTituloContainingIgnoreCase(String nomeSerie);
}
