package com.study.spring6webclient.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.study.spring6webclient.model.BeerDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class BeerClientImpl implements BeerClient {

    public static final String BEER_PATH = "/api/v3/beers";
    public static final String BEER_ID_PATH = BEER_PATH + "/{id}";

    private final WebClient webClient;

    public BeerClientImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Flux<BeerDTO> getAllBeers() {
        return webClient.get().uri(BEER_PATH)
                .retrieve()
                .bodyToFlux(BeerDTO.class);
    }

    @Override
    public Flux<String> getAllBeersAsString() {
        return webClient.get().uri(BEER_PATH)
                .retrieve()
                .bodyToFlux(String.class);
    }

    @Override
    public Flux<Map> getAllBeersAsMap() {
        return webClient.get().uri(BEER_PATH)
                .retrieve()
                .bodyToFlux(Map.class);
    }

    @Override
    public Flux<JsonNode> getAllBeersAsJson() {
        return webClient.get().uri(BEER_PATH)
                .retrieve()
                .bodyToFlux(JsonNode.class);
    }

    @Override
    public Mono<BeerDTO> getBeerById(String id) {
        return webClient.get().uri(uriBuilder ->
                        uriBuilder.path(BEER_ID_PATH).build(id))
                .retrieve()
                .bodyToMono(BeerDTO.class);
    }

    @Override
    public Flux<BeerDTO> getByBeerName(String beerName) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(BEER_PATH)
                        .queryParam("beerName", beerName)
                        .build())
                .retrieve()
                .bodyToFlux(BeerDTO.class);
    }

    @Override
    public Flux<BeerDTO> getByBeerStyle(String beerStyle) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(BEER_PATH)
                        .queryParam("beerStyle", beerStyle)
                        .build())
                .retrieve()
                .bodyToFlux(BeerDTO.class);
    }

    @Override
    public Mono<BeerDTO> createBeer(BeerDTO beerDTO) {
        return webClient.post().uri(BEER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(beerDTO)
                .retrieve()
                .toBodilessEntity()
                .flatMap(voidResponseEntity -> Mono.just(voidResponseEntity
                                .getHeaders()
                                .get(HttpHeaders.LOCATION).getFirst())
                        .map(path -> {
                            String[] split = path.split("/");
                            return split[split.length - 1];
                        }))
                .flatMap(this::getBeerById);
    }

    @Override
    public Mono<BeerDTO> updateBeer(String id, BeerDTO beerDTO) {
        return webClient.put().uri(BEER_ID_PATH, id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(beerDTO)
                .retrieve()
                .toBodilessEntity()
                .flatMap(voidResponseEntity -> this.getBeerById(id));
    }

    @Override
    public Mono<BeerDTO> patchBeer(String id, BeerDTO beerDTO) {
        return webClient.patch().uri(BEER_ID_PATH, id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(beerDTO)
                .retrieve()
                .toBodilessEntity()
                .flatMap(voidResponseEntity -> this.getBeerById(id));
    }

    @Override
    public Mono<Void> deleteBeer(String id) {
        return webClient.delete().uri(BEER_ID_PATH, id)
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
