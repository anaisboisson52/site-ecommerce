package com.epsi.site_ecommerce.service;

import com.epsi.site_ecommerce.dto.Product;
import com.epsi.site_ecommerce.repository.ProductRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ProductServiceTest {

    static MockWebServer mockWebServer;
    ProductService productService;
    ProductRepository productRepository; // on va le mocker pour saveProducts

    @BeforeAll
    static void startServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void stopServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        productService = new ProductService(
                productRepository,
                org.springframework.web.reactive.function.client.WebClient.builder().baseUrl(mockWebServer.url("/").toString()),
                mockWebServer.url("/").toString()
        );
    }

    @Test
    void getAllProducts_shouldReturnProducts() {
        String responseBody = """
            [
              {"id":"1","name":"Produit A"},
              {"id":"2","name":"Produit B"}
            ]
        """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        List<Product> result = productService.getAllProducts();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("1");
        assertThat(result.get(1).getName()).isEqualTo("Produit B");
    }

    @Test
    void getAllProducts_shouldThrow404_whenNotFound() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404).setBody("Introuvable"));

        Throwable ex = catchThrowable(() -> productService.getAllProducts());

        assertThat(ex).isInstanceOf(org.springframework.web.server.ResponseStatusException.class)
                .hasMessageContaining("Produit non trouvé");
    }

    @Test
    void getAllProducts_shouldThrow5xx_whenServerError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Server error"));

        Throwable ex = catchThrowable(() -> productService.getAllProducts());

        assertThat(ex).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erreur serveur");
    }

    @Test
    void getAllProductsByPagination_shouldReturnFirstPage() {
        String responseBody = """
            [
              {"id":"1","name":"Produit A"},
              {"id":"2","name":"Produit B"},
              {"id":"3","name":"Produit C"}
            ]
        """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        Page<Product> page = productService.getAllProductsByPagination(0, 2);
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    @Test
    void getAllProductsByPagination_shouldReturnEmptyPageWhenOutOfRange() {
        String responseBody = """
            [
              {"id":"1","name":"Produit A"},
              {"id":"2","name":"Produit B"}
            ]
        """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        Page<Product> page = productService.getAllProductsByPagination(2, 5);
        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void getProductById_shouldReturnProduct() {
        String responseBody = """
            [
              {"id":"42","name":"Produit Unique"}
            ]
        """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        Product result = productService.getProductById("42");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("42");
        assertThat(result.getName()).isEqualTo("Produit Unique");
    }

    @Test
    void getProductById_shouldThrow404_whenNotFound() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404).setBody("Not Found"));

        Throwable ex = catchThrowable(() -> productService.getProductById("999"));

        assertThat(ex).isInstanceOf(org.springframework.web.server.ResponseStatusException.class)
                .hasMessageContaining("Produit non trouvé");
    }


}
