package com.unir.videojuegoselasticsearch.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoJuegoRequest {

	private String titulo;
	private String descripcion;
	private Double precio;
}
