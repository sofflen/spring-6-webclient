package com.study.spring6webclient.client;

import com.study.spring6webclient.model.BeerDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BeerClientImplTest {

    @Autowired
    BeerClient beerClient;

    @Test
    @Order(1)
    void testGetAllBeers() {
        StepVerifier.create(beerClient.getAllBeers())
                .recordWith(ArrayList::new)
                .expectNextCount(3)
                .consumeRecordedWith(beerList -> beerList.forEach(System.out::println))
                .verifyComplete();
    }

    @Test
    @Order(1)
    void testGetAllBeersAsString() {
        StepVerifier.create(beerClient.getAllBeersAsString())
                .consumeNextWith(System.out::println)
                .verifyComplete();
    }

    @Test
    @Order(1)
    void testGetAllBeersAsMap() {
        StepVerifier.create(beerClient.getAllBeersAsMap())
                .recordWith(ArrayList::new)
                .expectNextCount(3)
                .consumeRecordedWith(beerList -> beerList.forEach(System.out::println))
                .verifyComplete();
    }

    @Test
    @Order(1)
    void testGetAllBeersAsJson() {
        StepVerifier.create(beerClient.getAllBeersAsJson())
                .recordWith(ArrayList::new)
                .expectNextCount(3)
                .consumeRecordedWith(jsonNodeList -> jsonNodeList
                        .forEach(jsonNode ->
                                System.out.println(jsonNode.toPrettyString())))
                .verifyComplete();
    }

    @Test
    @Order(1)
    void testGetBeerById() {
        StepVerifier.create(beerClient
                        .getAllBeers()
                        .next()
                        .flatMap(dto -> beerClient.getBeerById(dto.getId())))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @Order(1)
    void testGetByBeerName() {
        String beerNameQueryParam = "Crank";

        StepVerifier.create(beerClient.getByBeerName(beerNameQueryParam))
                .expectNextMatches(beerDto ->
                        beerNameQueryParam.equals(beerDto.getBeerName()))
                .verifyComplete();
    }

    @Test
    @Order(1)
    void testGetByBeerStyle() {
        String beerStyleQueryParam = "Pale Ale";

        StepVerifier.create(beerClient.getByBeerStyle(beerStyleQueryParam))
                .recordWith(ArrayList::new)
                .expectNextCount(2)
                .expectRecordedMatches(beerDTOS -> beerDTOS.stream()
                        .allMatch(beerDTO -> beerStyleQueryParam.equals(beerDTO.getBeerStyle())))
                .verifyComplete();
    }

    @Test
    @Order(2)
    void testCreateBeer() {
        BeerDTO testBeerDto = createTestBeerDTO();

        StepVerifier.create(beerClient.createBeer(testBeerDto)
                        .flatMap(createdDto -> {
                            testBeerDto.setId(createdDto.getId());
                            return beerClient.getBeerById(createdDto.getId());
                        }))
                .expectNext(testBeerDto)
                .verifyComplete();

        beerClient.deleteBeer(testBeerDto.getId()).subscribe();
    }

    @Test
    @Order(999)
    void testUpdateBeer() {
        BeerDTO testBeerDto = createTestBeerDTO();
        StepVerifier.create(beerClient.getAllBeers()
                        .next()
                        .flatMap(dto -> {
                            var beerDTOMono = beerClient.updateBeer(dto.getId(), testBeerDto);
                            testBeerDto.setId(dto.getId());
                            return beerDTOMono;
                        }))
                .expectNext(testBeerDto)
                .verifyComplete();
    }

    @Test
    @Order(999)
    void testPatchBeer() {
        BeerDTO testBeerDto = createTestBeerDTO();
        StepVerifier.create(beerClient.getAllBeers()
                        .next()
                        .flatMap(dto -> {
                            var beerDTOMono = beerClient.patchBeer(dto.getId(), testBeerDto);
                            testBeerDto.setId(dto.getId());
                            return beerDTOMono;
                        }))
                .expectNext(testBeerDto)
                .verifyComplete();
    }

    @Test
    @Order(2)
    void testDeleteBeer() {
        AtomicReference<BeerDTO> deletedBeerDTO = new AtomicReference<>();

        StepVerifier.create(beerClient.getAllBeers()
                .next()
                .flatMap(dto -> {
                    deletedBeerDTO.set(dto);
                    return beerClient.deleteBeer(dto.getId());
                }))
                .verifyComplete();

        beerClient.createBeer(deletedBeerDTO.get()).subscribe();
    }

    private BeerDTO createTestBeerDTO() {
        return BeerDTO.builder()
                .beerName("Space Dust")
                .beerStyle("IPA")
                .price(new BigDecimal("10.99"))
                .quantityOnHand(44)
                .upc("123321")
                .build();
    }

}