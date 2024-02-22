package br.com.watch.screenmatch.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.watch.screenmatch.model.DadosSerie;
import br.com.watch.screenmatch.model.DadosTemporada;
import br.com.watch.screenmatch.model.Episodio;
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
	
	private SerieRepository repositorio;
	
	private List<Serie> series = new ArrayList<>();
	
	
	public Principal(SerieRepository repositorio) {
		this.repositorio = repositorio;
	}

	public void exibeMenu() {
		
		var opcao = -1;
		while (opcao != 0) {
			var menu = """
					1 - Buscar series
					2 - Buscar episódios
					3 - Listar series buscadas
					4 - Buscar serie por titulo
					5 - Buscar series por ator
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
			case 4: 
				buscarSeriePorTitulo();
				break;
			case 5: 
				buscarSeriesPorAtor();
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
		listarSeriesBuscadas();
		System.out.print("Escolha uma serie pelo nome: ");
		var nomeSerie = sc.nextLine();
		
		Optional<Serie> serie= repositorio.findByTituloContainingIgnoreCase(nomeSerie);
		
		if (serie.isPresent()) {
		
			var serieEncontrada = serie.get();
			List<DadosTemporada> temporadas = new ArrayList<>();
			
			for (int i = 1; i<=serieEncontrada.getTotalTemporadas(); i++) {
							
				var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);

				DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
				temporadas.add(dadosTemporada);
			}
			
			temporadas.forEach(System.out::println);
			
			List<Episodio> episodios = temporadas.stream()
				.flatMap(d -> d.episodios().stream()
						.map(e -> new Episodio(d.numeroTemporada(), e)))
				.collect(Collectors.toList());
			
			serieEncontrada.setEpisodios(episodios);
			repositorio.save(serieEncontrada);
		} else { 
			System.out.println("Serie não encontrada!");	
		}
	}
	
	private void listarSeriesBuscadas() {
		
		series = repositorio.findAll();
		series.stream()
				.sorted(Comparator.comparing(Serie::getGenero))
				.forEach(System.out::println);
	}
	
	private void buscarSeriePorTitulo() {
		System.out.print("Escolha uma serie pelo nome: ");
		var nomeSerie = sc.nextLine();
		
		Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
		
		if (serieBuscada.isPresent()) {
			System.out.println("Dados da serie: " + serieBuscada.get());
		} else {
			System.out.println("Serie nao encontrada!");
		}
	}
	
	private void buscarSeriesPorAtor() {
		System.out.print("Qual o nome para busca: ");
		var nomeAtor = sc.nextLine();
		
		System.out.print("Avaliacoes a partir de qual valor: ");
		var avaliacao = sc.nextDouble();
		
		List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
		System.out.println("Series em que " + nomeAtor + " trabalhou: ");
		seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " avaliacao: " + s.getAvaliacao()));
	}
}
