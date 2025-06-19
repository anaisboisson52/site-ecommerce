package com.epsi.site_ecommerce.controller;

import com.epsi.site_ecommerce.dto.CarrierOptionsRequest;
import com.epsi.site_ecommerce.dto.CarrierResponse;
import com.epsi.site_ecommerce.service.CarrierService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carriers")
public class ControllerCarriers {

    private final CarrierService carrierService;

    public ControllerCarriers(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    @PostMapping("/options")
    public List<CarrierResponse> getAvailableCarriers(@RequestBody CarrierOptionsRequest request) {
        return carrierService.getCarriersForWeight(request.weight());
    }
}
