package com.unir.videojuegoselasticsearch.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unir.videojuegoselasticsearch.model.db.VideoJuego;
import com.unir.videojuegoselasticsearch.model.request.VideoJuegoRequest;
import com.unir.videojuegoselasticsearch.model.response.VideoJuegoQueryResponse;
import com.unir.videojuegoselasticsearch.service.VideoJuegoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class VideoJuegoController {

	private final VideoJuegoService service;

	@GetMapping("/games")
	public ResponseEntity<VideoJuegoQueryResponse> getVideoJuegos(
			@RequestHeader Map<String, String> headers,
			@RequestParam(required = false) String title, 
			@RequestParam(required = false) String description, 
			@RequestParam(required = false) Double price,
			@RequestParam(required = false, defaultValue = "false") Boolean aggregate) {

		log.info("headers: {}", headers);
		VideoJuegoQueryResponse res = service.getVideoJuego(title, description, price);
		return ResponseEntity.ok(res);
	}

	@GetMapping("/games/{videoJuegoId}")
	public ResponseEntity<VideoJuego> getVideoJuego(@PathVariable String videoJuegoId) {

		VideoJuego videoJuego = service.getJuego(videoJuegoId);

		if (videoJuego != null) {
			return ResponseEntity.ok(videoJuego);
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@DeleteMapping("/games/{videoJuegoId}")
	public ResponseEntity<Void> deleteVideoJuego(@PathVariable String videoJuegoId) {

		Boolean removed = service.removeVideoJuego(videoJuegoId);

		if (Boolean.TRUE.equals(removed)) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@PostMapping("/games")
	public ResponseEntity<VideoJuego> getVideoJuego(@RequestBody VideoJuegoRequest request) {

		VideoJuego videoJuego = service.createVideoJuego(request);

		if (videoJuego != null) {
			return ResponseEntity.status(HttpStatus.CREATED).body(videoJuego);
		} else {
			return ResponseEntity.badRequest().build();
		}

	}

	//sugerencias
	@GetMapping("/games/suggestions")
    public List<String> getSuggestions(@RequestParam String title) {
        return service.getSuggestions(title);
    }

	//actualizacion de games
    @PutMapping("/games/{id}")
    public ResponseEntity<VideoJuego>  updateVideoJuego(@PathVariable String id, @RequestBody VideoJuego videoJuego) {
        if (!id.equals(videoJuego.getId())) {
            return ResponseEntity.badRequest().build();
        }
        service.updateVideoJuego(videoJuego);
		return ResponseEntity.status(HttpStatus.OK).body(videoJuego);
    }


    @GetMapping("/games/facets/prices")
    public ResponseEntity<List<String>> getCountryFacets() {
        List<String> countryFacets = service.getPrecioFacets();
        return ResponseEntity.ok(countryFacets);
    }

	//agrupamiento 
    @GetMapping("/games/facets/price/count")
    public ResponseEntity<Long> getPriceFacestCount(@RequestParam Double price) {
        Long cantidad = service.getPriceFacestCount(price);
        return ResponseEntity.ok(cantidad);
    }
	
}
