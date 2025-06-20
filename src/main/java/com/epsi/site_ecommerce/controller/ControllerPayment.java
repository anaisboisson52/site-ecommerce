package com.epsi.site_ecommerce.controller;

import com.epsi.site_ecommerce.dto.Panier;
import com.epsi.site_ecommerce.dto.PaymentListResponse;
import com.epsi.site_ecommerce.dto.PaymentResponse;
import com.epsi.site_ecommerce.dto.Product;
import com.epsi.site_ecommerce.service.PaymentService;
import jakarta.servlet.http.HttpSession;
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
    public PaymentListResponse getAllPayments(HttpSession session) {
        return paymentService.getPaymentListWithTotal(session);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable String id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<PaymentResponse> addPayment(@RequestBody PaymentResponse request) {
        return ResponseEntity.ok(paymentService.addPayment(request));
    }
}
