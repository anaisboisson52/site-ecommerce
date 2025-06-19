package com.epsi.site_ecommerce.service;

import com.epsi.site_ecommerce.dto.CarrierResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Service
public class CarrierService {

    private final WebClient webClient;
    private final String carriersEndpoint;

    public CarrierService(
            WebClient.Builder webClientBuilder,
            @Value("${webclient.base-url}") String baseUrl
    ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.carriersEndpoint = "/carriers";
    }

    /**
     * Retourne seulement les transporteurs compatibles avec le poids
     */
    public List<CarrierResponse> getCarriersForWeight(double weight) {
        CarrierResponse[] carriers = webClient.get()
                .uri(carriersEndpoint)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxError)
                .bodyToMono(CarrierResponse[].class)
                .block();

        if (carriers == null) return List.of();

        return Arrays.stream(carriers)
                .filter(carrier -> weight <= carrier.maxWeight())
                .toList();
    }

    private Mono<Throwable> handle4xxError(ClientResponse response) {
        return response.bodyToMono(String.class).map(body -> {
            if (response.statusCode().value() == 404) {
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Transporteur non trouvé : " + body);
            }
            return new ResponseStatusException(response.statusCode(), "Erreur client : " + body);
        });
    }

    private Mono<Throwable> handle5xxError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .map(body -> new RuntimeException("Erreur serveur : " + body));
    }
}
