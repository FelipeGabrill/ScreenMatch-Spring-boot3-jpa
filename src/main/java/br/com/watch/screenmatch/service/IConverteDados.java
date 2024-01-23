package br.com.watch.screenmatch.service;

public interface IConverteDados {
	
	<T> T obterDados(String json, Class<T> classe);

}
