package br.com.watch.screenmatch.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.watch.screenmatch.model.DadosSerie;
import br.com.watch.screenmatch.model.DadosTemporada;
import br.com.watch.screenmatch.model.Serie;
import br.com.watch.screenmatch.repository.SerieRepository;
import br.com.watch.screenmatch.service.ConsumoApi;
import br.com.watch.screenmatch.service.ConverteDados;

public class Principal {
	
	public Scanner sc = new Scanner(System.in);
	private ConsumoApi consumo = new ConsumoApi();
	private ConverteDados conversor = new ConverteDados();
	
	private final String ENDERECO = "http://www.omdbapi.com/?t=";
	private final String API_KEY = "&apikey=9c30019";

	private List<DadosSerie> dadosSeries = new ArrayList<>();
	
	private SerieRepository repositorio;
	
	
	public Principal(SerieRepository repositorio) {
		this.repositorio = repositorio;
	}

	public void exibeMenu() {
		
		var opcao = -1;
		while (opcao != 0) {
			var menu = """
					1 - Buscar séries
					2 - Buscar episódios
					3 - Listar séries buscadas
					
					0 - Sair
					""";
			System.out.println(menu);
			opcao = sc.nextInt();
			sc.nextLine();
			
			switch (opcao) {
			
			case 1: 
				buscarSerieWeb();
				break;
			case 2: 
				buscarEpisodiosPorSerie();
				break;
			case 3: 
				listarSeriesBuscadas();
				break;
			case 0: 
				System.out.println("Saindo...");
				break;
			default: 
				System.out.println("Opcao invalida");
			
			}
		}	
	}	
			
	private void buscarSerieWeb() {
		DadosSerie dados = getDadosSerie();
		Serie serie = new Serie(dados);
		//dadosSeries.add(dados);
		repositorio.save(serie);
		System.out.println(dados);	
	}	

	private DadosSerie getDadosSerie() {
		System.out.print("Digite o nome da série para busca: ");
		var nomeSerie = sc.nextLine();
		var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		return dados;
	}
	
	private void buscarEpisodiosPorSerie() {
		DadosSerie dadosSerie = getDadosSerie();
		List<DadosTemporada> temporadas = new ArrayList<>();
		
			for (int i = 1; i<=dadosSerie.totalTemporadas(); i++) {
				var json = consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
					
				DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
				temporadas.add(dadosTemporada);
			}
			
		temporadas.forEach(System.out::println);
	}
	
	private void listarSeriesBuscadas() {
		
		List<Serie> series = new ArrayList<>();
		series = dadosSeries.stream()
				.map(d -> new Serie(d))
				.collect(Collectors.toList());
		
		series.stream()
				.sorted(Comparator.comparing(Serie::getGenero))
				.forEach(System.out::println);
	}
}
