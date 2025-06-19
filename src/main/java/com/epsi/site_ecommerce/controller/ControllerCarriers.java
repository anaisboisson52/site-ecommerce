package com.epsi.site_ecommerce.controller;

import com.epsi.site_ecommerce.dto.CarrierOptionsRequest;
import com.epsi.site_ecommerce.dto.CarrierResponse;
import com.epsi.site_ecommerce.service.CarrierApiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carriers")
public class ControllerCarriers {

    private final CarrierApiService carrierApiService;

    public ControllerCarriers(CarrierApiService carrierApiService) {
        this.carrierApiService = carrierApiService;
    }

    @PostMapping("/options")
    public List<CarrierResponse> getAvailableCarriers(@RequestBody CarrierOptionsRequest request) {
        return carrierApiService.getCarriersForWeight(request.weight());
    }
}
