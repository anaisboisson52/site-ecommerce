package com.epsi.site_ecommerce.service;

import com.epsi.site_ecommerce.dto.Product;
import com.epsi.site_ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final WebClient webClient;

    public ProductService(ProductRepository productRepository, WebClient.Builder webClientBuilder,
                          @Value("${webclient.base-url}") String baseUrl) {
        this.productRepository = productRepository;
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public List<Product> getAllProducts() {
        return webClient.get()
                .uri("/products")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxError)
                .bodyToFlux(Product.class)
                .collectList()
                .block();
    }


    public Page<Product> getAllProductsByPagination(int page, int size) {
        List<Product> allProducts = webClient.get()
                .uri("/products")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxError)
                .bodyToFlux(Product.class)
                .collectList()
                .block();

        int start = page * size;
        int end = Math.min(start + size, allProducts.size());

        if (start > allProducts.size()) {
            return new PageImpl<>(List.of(), PageRequest.of(page, size), allProducts.size());
        }

        List<Product> paginated = allProducts.subList(start, end);
        return new PageImpl<>(paginated, PageRequest.of(page, size), allProducts.size());
    }

    public Product getProductById(String id) {
        return webClient.get()
                .uri("/products/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), this::handle4xxError)
                .onStatus(status -> status.is5xxServerError(), this::handle5xxError)
                .bodyToFlux(Product.class)
                .blockFirst();

    }

    public void updateStock(String productId, int newStock) {
        webClient.patch()
                .uri("/products/{id}", productId)
                .bodyValue("{\"stock\": " + newStock + "}")
                .header("Content-Type", "application/json")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxError)
                .bodyToMono(Void.class)
                .block();
    }

    public void updateProductStock(String productId, int nouveauStock) {
        webClient.patch()
                .uri("/products/{id}", productId)
                .header("Content-Type", "application/json")
                .bodyValue("{\"stock\":" + nouveauStock + "}")
                .retrieve()
                .toBodilessEntity()
                .block();
    }



    private Mono<Throwable> handle4xxError(ClientResponse response) {
        return response.bodyToMono(String.class).map(body -> {
            if (response.statusCode().value() == 404) {
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit non trouvé : " + body);
            }
            return new ResponseStatusException(response.statusCode(), "Erreur client : " + body);
        });
    }


    private Mono<Throwable> handle5xxError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .map(body -> new RuntimeException("Erreur serveur : " + body));
    }
}
