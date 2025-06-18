package com.epsi.site_ecommerce.controller;

import com.epsi.site_ecommerce.dto.ShippingForm;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class ControllerOrder {

    private static final String SESSION_SHIPPING_FORM = "shippingForm";

    @PostMapping("/shipping")
    public ResponseEntity<?> submitShippingForm(
            @Valid @RequestBody ShippingForm form,
            HttpSession session
    ) {
        session.setAttribute(SESSION_SHIPPING_FORM, form);

        return ResponseEntity.ok(form);
    }

    /** Récupérer les données de livraison stockées en session */
    @GetMapping("/shipping")
    public ResponseEntity<?> getShippingForm(HttpSession session) {
        ShippingForm form = (ShippingForm) session.getAttribute(SESSION_SHIPPING_FORM);

        if (form == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(form);
    }
}
