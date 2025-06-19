package com.epsi.site_ecommerce.service;

import com.epsi.site_ecommerce.dto.CarrierResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarrierApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String CARRIER_API_URL = "http://localhost:3000/carriers";

    /**
     * Retourne seulement les transporteurs compatibles avec le poids
     */
    public List<CarrierResponse> getCarriersForWeight(double weight) {
        CarrierResponse[] carriers = restTemplate.getForObject(CARRIER_API_URL, CarrierResponse[].class);
        if (carriers == null) return List.of();

        return Arrays.stream(carriers)
                .filter(carrier -> weight <= carrier.maxWeight())
                .collect(Collectors.toList());
    }
}
