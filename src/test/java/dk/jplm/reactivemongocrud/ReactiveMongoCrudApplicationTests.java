package dk.jplm.reactivemongocrud;

import dk.jplm.reactivemongocrud.controller.ProductController;
import dk.jplm.reactivemongocrud.dto.ProductDto;
import dk.jplm.reactivemongocrud.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static reactor.core.publisher.Mono.when;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(ProductController.class)
class ReactiveMongoCrudApplicationTests {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private ProductService service;

    @Test
    void addProductTest() {
        Mono<ProductDto> productDtoMono = Mono.just(new ProductDto("12", "JPLM", 9, 1337));
        when(service.saveProduct(productDtoMono))
                .thenReturn(productDtoMono);

        webTestClient.post().uri("/products")
                .body(Mono.just(productDtoMono), ProductDto.class)
                .exchange()
                .expectStatus()
                .isOk(); // status code 200
    }

    @Test
    void getProductsTest() {
        Flux<ProductDto> productDtoFlux = Flux.just(new ProductDto("12", "JPLM", 9, 1337),
                new ProductDto("9", "Car", 1, 1000000));
        when(service.getProducts()).thenReturn(productDtoFlux);

        Flux<ProductDto> responseBody = webTestClient.get().uri("/products")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ProductDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNext(new ProductDto("12", "JPLM", 9, 1337))
                .expectNext(new ProductDto("9", "Car", 1, 1000000))
                .verifyComplete();

    }


    @Test
    void getProductTest() {
        Mono<ProductDto> productDtoMono = Mono.just(new ProductDto("12", "JPLM", 9, 1337));
        when(service.getProduct(any())).thenReturn(productDtoMono);

        Flux<ProductDto> responseBody = webTestClient.get().uri("/products/12")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ProductDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNextMatches(p -> p.getName().equals("JPLM"))
                .verifyComplete();
    }


    @Test
    void updateProductTest() {
        Mono<ProductDto> productDtoMono = Mono.just(new ProductDto("12", "JP", 9, 1337));
        when(service.updateProduct(productDtoMono, "12")).thenReturn(productDtoMono);

        webTestClient.put().uri("/products/update/12")
                .body(Mono.just(productDtoMono), ProductDto.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void deleteProductTest() {
        given(service.deleteProduct(any()))
                .willReturn(Mono.empty());

        webTestClient.delete().uri("/products/delete/12")
                .exchange()
                .expectStatus()
                .isOk();
    }

}
