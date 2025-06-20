package com.epsi.site_ecommerce.service;

import com.epsi.site_ecommerce.dto.Panier;
import com.epsi.site_ecommerce.dto.PaymentListResponse;
import com.epsi.site_ecommerce.dto.PaymentResponse;
import com.epsi.site_ecommerce.dto.Product;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PaymentService {

    private final WebClient webClient;
    private final String paymentEndpoint;
    private final List<PaymentResponse> localPayments = new ArrayList<>();

    public PaymentService(
            WebClient.Builder webClientBuilder,
            @Value("${webclient.base-url}") String baseUrl
    ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.paymentEndpoint = "/payments";
    }

    public List<PaymentResponse> getPayment() {

        PaymentResponse[] remotePayments = webClient.get()
                .uri(paymentEndpoint)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxError)
                .bodyToMono(PaymentResponse[].class)
                .block();


        List<PaymentResponse> allPayments = new ArrayList<>();
        if (remotePayments != null) {
            allPayments.addAll(Arrays.asList(remotePayments));
        }
        allPayments.addAll(localPayments);

        return allPayments;
    }

    public PaymentListResponse getPaymentListWithTotal(HttpSession session) {
        List<PaymentResponse> payments = getPayment();

        Panier panier = (Panier) session.getAttribute("panier");
        double subtotal = (panier != null) ? panier.getTotal() : 0.0;

        Object carrierObj = session.getAttribute("selectedCarrier");
        double carrierPrice = 0.0;
        if (carrierObj instanceof com.epsi.site_ecommerce.dto.CarrierResponse carrier) {
            carrierPrice = carrier.price();
        }

        double total = subtotal + carrierPrice;

        return new PaymentListResponse(payments, total);
    }



    public PaymentResponse getPaymentById(String id) {
        return webClient.get()
                .uri("/payments/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), this::handle4xxError)
                .onStatus(status -> status.is5xxServerError(), this::handle5xxError)
                .bodyToFlux(PaymentResponse.class)
                .blockFirst();

    }

    public PaymentResponse addPayment(PaymentResponse payment) {
        localPayments.add(payment);
        return payment;
    }

    private Mono<Throwable> handle4xxError(ClientResponse response) {
        return response.bodyToMono(String.class).map(body -> {
            if (response.statusCode().value() == 404) {
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Paiement non trouvé : " + body);
            }
            return new ResponseStatusException(response.statusCode(), "Erreur client : " + body);
        });
    }

    private Mono<Throwable> handle5xxError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .map(body -> new RuntimeException("Erreur serveur : " + body));
    }
}
