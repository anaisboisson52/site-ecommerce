package com.epsi.site_ecommerce.service;

import com.epsi.site_ecommerce.entity.ProductEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ProductService {

    private final WebClient webClient;

    public ProductService(WebClient.Builder webClientBuilder,
                          @org.springframework.beans.factory.annotation.Value("${webclient.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public List<ProductEntity> getAllProducts() {
        return webClient.get()
                .uri("/products")
                .retrieve()
                .bodyToFlux(ProductEntity.class)
                .collectList()
                .block(); // blocage volontaire pour test
    }
}
