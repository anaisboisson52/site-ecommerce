package com.epsi.site_ecommerce.controller;

import com.epsi.site_ecommerce.dto.PaymentResponse;
import com.epsi.site_ecommerce.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class ControllerPayment {

    private final PaymentService paymentService;

    public ControllerPayment(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayment() {
        return ResponseEntity.ok(paymentService.getPayment());
    }

    @PostMapping("/add")
    public ResponseEntity<PaymentResponse> addPayment(@RequestBody PaymentResponse request) {
        return ResponseEntity.ok(paymentService.addPayment(request));
    }
}
