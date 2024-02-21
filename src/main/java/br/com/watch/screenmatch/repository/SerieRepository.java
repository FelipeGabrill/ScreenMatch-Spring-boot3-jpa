package br.com.watch.screenmatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.watch.screenmatch.model.Serie;

public interface SerieRepository extends JpaRepository<Serie, Long> {
	
}
