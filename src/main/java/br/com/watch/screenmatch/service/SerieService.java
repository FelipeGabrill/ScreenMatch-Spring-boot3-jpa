package br.com.watch.screenmatch.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.watch.screenmatch.dto.SerieDTO;
import br.com.watch.screenmatch.repository.SerieRepository;

@Service
public class SerieService {
	
	@Autowired
	private SerieRepository repositorio;
	
	public List<SerieDTO> obterTodasAsSeries() {
		return repositorio.findAll()
				.stream()
				.map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse()))
				.collect(Collectors.toList());
	}
}
