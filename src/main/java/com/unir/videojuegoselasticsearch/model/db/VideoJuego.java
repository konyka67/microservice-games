package com.unir.videojuegoselasticsearch.model.db;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(indexName = "videojuegos", createIndex = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class VideoJuego {
	
	@Id
	private String id;
	
	@Field(type = FieldType.Text, name = "titulo")
	private String titulo;

	@Field(type = FieldType.Search_As_You_Type, name = "descripcion")
	private String descripcion;
	
	@Field(type = FieldType.Keyword, name = "precio")
	private Double precio;



}
