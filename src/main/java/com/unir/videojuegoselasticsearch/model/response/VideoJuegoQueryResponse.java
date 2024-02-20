package com.unir.videojuegoselasticsearch.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.unir.videojuegoselasticsearch.model.db.VideoJuego;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VideoJuegoQueryResponse {

    private List<VideoJuego> videoJuegos;
    private List<AggregationDetails> aggs;

}
