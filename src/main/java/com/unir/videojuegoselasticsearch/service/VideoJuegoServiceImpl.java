package com.unir.videojuegoselasticsearch.service;

import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.unir.videojuegoselasticsearch.data.DataAccessRepository;
import com.unir.videojuegoselasticsearch.model.db.VideoJuego;
import com.unir.videojuegoselasticsearch.model.request.VideoJuegoRequest;
import com.unir.videojuegoselasticsearch.model.response.VideoJuegoQueryResponse;

@Service
@RequiredArgsConstructor
public class VideoJuegoServiceImpl implements VideoJuegoService {

	private final DataAccessRepository repository;

	@Override
	public VideoJuegoQueryResponse getVideoJuego(String titulo, String descripcion, Double precio) {
		return repository.buscarVideoJuegos(titulo, descripcion, precio);
	}

	@Override
	public VideoJuego createVideoJuego(VideoJuegoRequest request) {

			if (request != null && StringUtils.hasLength(request.getTitulo().trim())
					&& StringUtils.hasLength(request.getDescripcion().trim())
					&& request.getPrecio() != null && request.getPrecio().doubleValue() != 0.0) {
	
				VideoJuego videoJuego = VideoJuego.builder().titulo(request.getTitulo()).descripcion(request.getDescripcion())
						.precio(request.getPrecio()).build();
	
				return repository.save(videoJuego);
			} else {
				return null;
			}
	}
	

	@Override
	public List<String> getSuggestions(String titulo) {
		// TODO Auto-generated method stub
		return repository.getSuggestions(titulo);
	}

	@Override
	public void updateVideoJuego(VideoJuego videoJuego) {
		repository.updatedVideoJuego(videoJuego);
	}

	@Override
	public List<String> getPrecioFacets() {
		// TODO Auto-generated method stub
		return repository.getPrecioFacets();
	}

	@Override
	public VideoJuego getJuego(String videoJuegoId) {
		// TODO Auto-generated method stub
		return repository.findById(videoJuegoId).orElse(null);
	}

	@Override
	public Long getPriceFacestCount(Double price) {
		// TODO Auto-generated method stub
		return repository.getPriceFacestCount(price);
	}

	@Override
	public Boolean removeVideoJuego(String videoJuegoId) {
		VideoJuego videoJuego = repository.findById(videoJuegoId).orElse(null);
		if (videoJuego != null) {
			repository.delete(videoJuego);
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}


	
}
