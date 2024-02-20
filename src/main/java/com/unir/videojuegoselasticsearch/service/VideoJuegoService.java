package com.unir.videojuegoselasticsearch.service;

import java.util.List;

import com.unir.videojuegoselasticsearch.model.db.VideoJuego;
import com.unir.videojuegoselasticsearch.model.request.VideoJuegoRequest;
import com.unir.videojuegoselasticsearch.model.response.VideoJuegoQueryResponse;

public interface VideoJuegoService {

	VideoJuegoQueryResponse getVideoJuego(String titulo, String descripcion, Double precio);
	
	
	VideoJuego createVideoJuego(VideoJuegoRequest request);

	public List<String> getSuggestions(String titulo) ;

	public void updateVideoJuego(VideoJuego videoJuego);

	public List<String> getPrecioFacets();

	VideoJuego getJuego(String videoJuegoId);

	public Long getPriceFacestCount(Double titulo);

	Boolean removeVideoJuego(String videoJuegoId);
}
