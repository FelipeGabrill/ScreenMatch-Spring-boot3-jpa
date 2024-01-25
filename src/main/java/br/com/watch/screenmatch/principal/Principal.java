package br.com.watch.screenmatch.principal;

import java.util.Scanner;

import br.com.watch.screenmatch.service.ConsumoApi;

public class Principal {
	
	public Scanner scan = new Scanner(System.in);
	private ConsumoApi consumo = new ConsumoApi();
	
	private final String ENDERECO = "http://www.omdbapi.com/?t=";
	private final String API_KEY = "&apikey=9c30019";

	public void exibeMenu() {
		System.out.print("Digite o nome da série para busca: ");
		var nomeSerie = scan.nextLine();
		var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
	}
	
}
