package com.epsi.site_ecommerce.service;

import com.epsi.site_ecommerce.dto.CarrierResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class CarrierServiceTest {

    static MockWebServer mockWebServer;
    CarrierService carrierService;

    private static CarrierResponse buildCarrier(String id, String name, double maxWeight) {
        return new CarrierResponse(
                id, name, "Service", "Zone", 4.5, maxWeight,
                "contact@email.com", "+33123456789", "https://track/{tracking_number}",
                List.of("option1", "option2"),3
        );
    }

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
        carrierService = new CarrierService(
                org.springframework.web.reactive.function.client.WebClient.builder().baseUrl(mockWebServer.url("/").toString()),
                mockWebServer.url("/").toString()
        );
    }

    @Test
    void getCarriersForWeight_shouldReturnCompatibleCarriers() {
        String responseBody = """
            [
              {
                "id": "trk1", "name": "Express", "service_type": "Rapide", "area_served": "FR",
                "average_rating": 4.7, "max-weight": 2, "contact_email": "contact@exp.com",
                "phone": "+331111", "tracking_url_template": "https://exp", "features": ["A", "B"]
              },
              {
                "id": "trk2", "name": "Cargo", "service_type": "Lourd", "area_served": "EU",
                "average_rating": 4.1, "max-weight": 10, "contact_email": "contact@cargo.com",
                "phone": "+332222", "tracking_url_template": "https://cargo", "features": ["X"]
              }
            ]
        """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        List<CarrierResponse> result = carrierService.getCarriersForWeight(3);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo("trk2");
    }

    @Test
    void getCarriersForWeight_shouldReturnAllIfAllCompatible() {
        String responseBody = """
            [
              {
                "id": "trk1", "name": "Express", "service_type": "Rapide", "area_served": "FR",
                "average_rating": 4.7, "max-weight": 10, "contact_email": "contact@exp.com",
                "phone": "+331111", "tracking_url_template": "https://exp", "features": []
              },
              {
                "id": "trk2", "name": "Cargo", "service_type": "Lourd", "area_served": "EU",
                "average_rating": 4.1, "max-weight": 12, "contact_email": "contact@cargo.com",
                "phone": "+332222", "tracking_url_template": "https://cargo", "features": []
              }
            ]
        """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        List<CarrierResponse> result = carrierService.getCarriersForWeight(5);

        assertThat(result).hasSize(2);
    }

    @Test
    void getCarriersForWeight_shouldReturnEmptyIfNoneCompatible() {
        String responseBody = """
            [
              {
                "id": "trk1", "name": "Express", "service_type": "Rapide", "area_served": "FR",
                "average_rating": 4.7, "max-weight": 2, "contact_email": "contact@exp.com",
                "phone": "+331111", "tracking_url_template": "https://exp", "features": []
              }
            ]
        """;
        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        List<CarrierResponse> result = carrierService.getCarriersForWeight(7);

        assertThat(result).isEmpty();
    }

    @Test
    void getCarriersForWeight_shouldThrow404_whenNotFound() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404).setBody("Introuvable"));

        Throwable ex = catchThrowable(() -> carrierService.getCarriersForWeight(2));
        assertThat(ex).isInstanceOf(org.springframework.web.server.ResponseStatusException.class)
                .hasMessageContaining("Transporteur non trouvé");
    }

    @Test
    void getCarriersForWeight_shouldThrow5xx_whenServerError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Server error"));

        Throwable ex = catchThrowable(() -> carrierService.getCarriersForWeight(2));
        assertThat(ex).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erreur serveur");
    }
}
