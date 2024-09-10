package com.study.spring6webclient.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.study.spring6webclient.model.BeerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface BeerClient {

    Flux<BeerDTO> getAllBeers();

    Flux<String> getAllBeersAsString();

    Flux<Map> getAllBeersAsMap();

    Flux<JsonNode> getAllBeersAsJson();

    Mono<BeerDTO> getBeerById(String id);

    Flux<BeerDTO> getByBeerName(String beerName);

    Flux<BeerDTO> getByBeerStyle(String beerStyle);

    Mono<BeerDTO> createBeer(BeerDTO beerDTO);

    Mono<BeerDTO> updateBeer(String id, BeerDTO beerDTO);

    Mono<BeerDTO> patchBeer(String id, BeerDTO beerDTO);

    Mono<Void> deleteBeer(String id);
}
