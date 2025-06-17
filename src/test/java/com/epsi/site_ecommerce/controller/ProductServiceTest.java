package com.epsi.site_ecommerce.controller;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class ProductServiceTest {

    @Autowired
    private MockMvc mockMvc;

    static MockWebServer mockWebServer;

    @BeforeAll
    static void startServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void stopServer() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("webclient.base-url", () -> mockWebServer.url("/").toString());
    }

    @TestConfiguration
    static class WebClientTestConfig {
        @Bean
        @Primary
        public WebClient testWebClient(@Value("${webclient.base-url}") String baseUrl) {
            return WebClient.builder().baseUrl(baseUrl).build();
        }
    }

    @Test
    void shouldReturn200AndGetAllProductsFromExternalApi() throws Exception {
        String responseBody = """
            [
              {
                "id": "1",
                "name": "Produit A",
                "descriptionShort": "Desc courte",
                "descriptionLong": "Desc longue",
                "price": 1000,
                "currency": "EUR",
                "stock": 5,
                "category": "Catégorie test",
                "weight": 200,
                "images": ["https://image.com/1.jpg"]
              }
            ]
            """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        mockMvc.perform(get("/product")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Produit A"));
    }

    @Test
    void shouldReturn404WhenExternalApiReturnsBadRequest() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json")
                .setBody("Bad request"));

        mockMvc.perform(get("/product")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Produit non trouvé : Bad request"));
    }

    @Test
    void shouldReturn200AndGetAllProductsByPagination() throws Exception {
        String responseBody = """
            [
              {
                "id": "1",
                "name": "Produit A",
                "descriptionShort": "Desc courte",
                "descriptionLong": "Desc longue",
                "price": 1000,
                "currency": "EUR",
                "stock": 5,
                "category": "Catégorie test",
                "weight": 200,
                "images": ["https://image.com/1.jpg"]
              },
              {
                "id": "2",
                "name": "Produit B",
                "descriptionShort": "Desc courte",
                "descriptionLong": "Desc longue",
                "price": 1000,
                "currency": "EUR",
                "stock": 5,
                "category": "Catégorie test",
                "weight": 200,
                "images": ["https://image.com/2.jpg"]
              }
            ]
            """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        mockMvc.perform(get("/product/paginated?page=0&size=2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("Produit A"));
    }

    @Test
    void shouldReturn400WhenPaginationValueIsNegative() throws Exception {
        mockMvc.perform(get("/product/paginated?page=0&size=-3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("must be greater than or equal to 1")));
    }
}
