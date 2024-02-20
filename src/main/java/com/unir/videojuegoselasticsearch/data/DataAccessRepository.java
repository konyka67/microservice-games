package com.unir.videojuegoselasticsearch.data;
import java.util.*;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

import com.unir.videojuegoselasticsearch.model.db.VideoJuego;
import com.unir.videojuegoselasticsearch.model.response.AggregationDetails;
import com.unir.videojuegoselasticsearch.model.response.VideoJuegoQueryResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DataAccessRepository {
    @Value("${server.fullAddress}")
    private String serverFullAddress;


    private final VideoJuegoRepository videoJuegoRepository;
    private final ElasticsearchOperations elasticClient;
  

    private final String[] descriptionSearchFields = {"descripcion", "descripcion._2gram", "descripcion._3gram"};

    public VideoJuego save(VideoJuego videoJuego) {
        return videoJuegoRepository.save(videoJuego);
    }

    public Boolean delete(VideoJuego videoJuego) {
        videoJuegoRepository.delete(videoJuego);
        return Boolean.TRUE;
    }

	public Optional<VideoJuego> findById(String id) {
		return videoJuegoRepository.findById(id);
	}
 

    @SneakyThrows
    public VideoJuegoQueryResponse buscarVideoJuegos(String titulo, String descripcion, Double precio) {

        BoolQueryBuilder querySpec = QueryBuilders.boolQuery();


        if (!StringUtils.isEmpty(titulo)) {
            querySpec.must(QueryBuilders.matchQuery("titulo", titulo));
        }


        if (precio != null && precio !=0.0) {
            querySpec.must(QueryBuilders.matchQuery("precio", precio));
        }
       

        if (!StringUtils.isEmpty(descripcion)) {
            querySpec.must(QueryBuilders.multiMatchQuery(descripcion, descriptionSearchFields).type(Type.BOOL_PREFIX));
        }

        if (!querySpec.hasClauses()) {
            querySpec.must(QueryBuilders.matchAllQuery());
        }
    

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(querySpec);

       
        Query query = nativeSearchQueryBuilder.build();
        SearchHits<VideoJuego> result = elasticClient.search(query, VideoJuego.class);

        List<AggregationDetails> responseAggs = new LinkedList<>();

        if (result.hasAggregations()) {
            Map<String, Aggregation> aggs = result.getAggregations().asMap();
            ParsedStringTerms countryAgg = (ParsedStringTerms) aggs.get("Precio Aggregation");

            String queryParams = getQueryParams(titulo, descripcion);
            countryAgg.getBuckets()
                    .forEach(
                            bucket -> responseAggs.add(
                                    new AggregationDetails(
                                            bucket.getKey().toString(),
                                            (int) bucket.getDocCount(),
                                            serverFullAddress + "/videojuegos?precio=" + bucket.getKey() + queryParams)));
        }
        return new VideoJuegoQueryResponse(result.getSearchHits().stream().map(SearchHit::getContent).toList(), responseAggs);
    }

    /**
     *      
     * @param titulo        
     * @param descripcion 
     * @return
     */
    private String getQueryParams(String titulo, String descripcion) {
        String queryParams = (StringUtils.isEmpty(titulo) ? "" : "&titulo=" + titulo)
                + (StringUtils.isEmpty(descripcion) ? "" : "&descripcion=" + descripcion);
      
        return queryParams.endsWith("&") ? queryParams.substring(0, queryParams.length() - 1) : queryParams;
    }

 
    public List<String> getSuggestions(String titulo) {
        List<String> suggestions = new ArrayList<>();
        
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.matchQuery("titulo", titulo)); 

        SearchHits<VideoJuego> searchHits = elasticClient.search(queryBuilder.build(), VideoJuego.class);
        
        for (SearchHit<VideoJuego> searchHit : searchHits) {
            VideoJuego videoJuego = searchHit.getContent();
            String suggestion = videoJuego.getTitulo();
            suggestions.add(suggestion);
        }
        
        return suggestions;
    }

    public void updatedVideoJuego(VideoJuego videoJuego) {
        IndexQueryBuilder indexQueryBuilder = new IndexQueryBuilder();
        indexQueryBuilder.withId(videoJuego.getId());
        indexQueryBuilder.withObject(videoJuego);

        String indexName = "videojuegos";

        elasticClient.index(indexQueryBuilder.build(), IndexCoordinates.of(indexName));
  
    }
    


    public List<String> getPrecioFacets() {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .addAggregation(AggregationBuilders.terms("precioFacets").field("precio").size(10))
                .build();

        SearchHits<VideoJuego> searchHits = elasticClient.search(searchQuery, VideoJuego.class);

        List<String> countryFacets = new ArrayList<>();
        Terms countryTerms = searchHits.getAggregations().get("precioFacets");
        for (Terms.Bucket bucket : countryTerms.getBuckets()) {
            countryFacets.add(bucket.getKeyAsString());
        }
        return countryFacets;
    }

    public Long getPriceFacestCount(Double price) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("precio", price))
                .build();

        SearchHits<VideoJuego> searchHits = elasticClient.search(searchQuery, VideoJuego.class);
        return searchHits.getTotalHits();
    }
 

}