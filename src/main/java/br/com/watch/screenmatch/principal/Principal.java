package br.com.watch.screenmatch.principal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.watch.screenmatch.model.DadosEpisodio;
import br.com.watch.screenmatch.model.DadosSerie;
import br.com.watch.screenmatch.model.DadosTemporada;
import br.com.watch.screenmatch.model.Episodio;
import br.com.watch.screenmatch.service.ConsumoApi;
import br.com.watch.screenmatch.service.ConverteDados;

public class Principal {
	
	public Scanner sc = new Scanner(System.in);
	private ConsumoApi consumo = new ConsumoApi();
	private ConverteDados conversor = new ConverteDados();
	
	private final String ENDERECO = "http://www.omdbapi.com/?t=";
	private final String API_KEY = "&apikey=9c30019";

	public void exibeMenu() {
		System.out.print("Digite o nome da série para busca: ");
		var nomeSerie = sc.nextLine();
		var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
		
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		System.out.println(dados);
		
		List<DadosTemporada> temporadas = new ArrayList<>();
			for (int i = 1; i<=dados.totalTemporadas(); i++) {
				json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
					
				DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
				temporadas.add(dadosTemporada);
			}
			
		temporadas.forEach(System.out::println);
		
		temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
		
		List<DadosEpisodio> dadosEpisodios = temporadas.stream()
				.flatMap(t -> t.episodios().stream())
				.collect(Collectors.toList());
		
		System.out.println("\nTop 5 episódios: ");
		
		dadosEpisodios.stream()
			.filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
			.sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
			.limit(5)
			.map(e -> e.titulo().toUpperCase())
			.forEach(System.out::println);
		
		List<Episodio> episodios = temporadas.stream()
				.flatMap(t -> t.episodios().stream()
						.map(d -> new Episodio(t.numeroTemporada(), d))
						)
				.collect(Collectors.toList());
		
		episodios.forEach(System.out::println);
		
//		System.out.print("Digite um trecho do titulo para busca: ");
//		var trechoTitulo = sc.nextLine().toUpperCase(); 
//		
//		Optional<Episodio> episodioBuscado = episodios.stream()
//				.filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo))
//				.findFirst();
//		
//		if (episodioBuscado.isPresent()) {
//			System.out.println("Episodio encontrado!");
//			System.out.println("Temporado " + episodioBuscado.get().getTemporada());
//		} else {
//			System.out.println("Episodio não encontrado!");
//		}
//		
//		System.out.print("A partir de que ano você deseja ver os episódios: ");
//		var ano = sc.nextInt();
//		sc.nextLine();
//		
//		LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//		
//		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
//		episodios.stream()
//			.filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//			.forEach (e -> System.out.println("Temporada: " + e.getTemporada() + " Episódio: " + e.getTitulo() + " Data lançamento: " + e.getDataLancamento().format(fmt)));
//		
		Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
				.filter(e -> e.getAvaliacao() > 0.0) 
				.collect(Collectors.groupingBy(Episodio::getTemporada, 
						Collectors.averagingDouble(Episodio::getAvaliacao)));
		
		System.out.println(avaliacoesPorTemporada);
		
		DoubleSummaryStatistics est = episodios.stream()
				.filter(e -> e.getAvaliacao() > 0.0) 
				.collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
		
		System.out.println("Media: " + est.getAverage());
		System.out.println("Melhor avaliado: " + est.getMax());
		System.out.println("Pior avaliado: " + est.getMin());
		System.out.println("Quantidade: " + est.getCount());

		
	}
	
}
