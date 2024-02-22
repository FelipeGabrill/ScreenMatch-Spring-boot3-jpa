package br.com.watch.screenmatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.watch.screenmatch.model.Categoria;
import br.com.watch.screenmatch.model.Serie;

public interface SerieRepository extends JpaRepository<Serie, Long> {
	Optional<Serie>findByTituloContainingIgnoreCase(String nomeSerie);

	List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);

	List<Serie> findTop5ByOrderByAvaliacaoDesc();
	
	List<Serie> findByGenero(Categoria categoria);
}
