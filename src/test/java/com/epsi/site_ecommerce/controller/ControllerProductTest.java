package com.epsi.site_ecommerce.controller;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = ControllerProductTest.MockServerInitializer.class)
class ControllerProductTest {

    @Autowired
    private MockMvc mockMvc;

    static MockWebServer mockWebServer;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    public static class MockServerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            try {
                String baseUrl = mockWebServer.url("/").toString();
                TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                        context,
                        "webclient.base-url=" + baseUrl
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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
}
