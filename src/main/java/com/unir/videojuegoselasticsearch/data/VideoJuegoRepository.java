package com.unir.videojuegoselasticsearch.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.unir.videojuegoselasticsearch.model.db.VideoJuego;

public interface VideoJuegoRepository extends ElasticsearchRepository<VideoJuego, String> {

	//List<VideoJuego> findByName(String titulo);
	
	Optional<VideoJuego> findById(String id);
	
	VideoJuego save(VideoJuego videoJuego);
	
	void delete(VideoJuego videoJuego);
	
	List<VideoJuego> findAll();
}
