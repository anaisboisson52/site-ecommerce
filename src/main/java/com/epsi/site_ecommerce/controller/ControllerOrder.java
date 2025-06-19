package com.epsi.site_ecommerce.controller;

import com.epsi.site_ecommerce.dto.Panier;
import com.epsi.site_ecommerce.dto.ShippingForm;
import com.epsi.site_ecommerce.service.PanierService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class ControllerOrder {

    private static final String SESSION_SHIPPING_FORM = "shippingForm";
    private static final String SESSION_PANIER = "panier";

    private final PanierService panierService;

    public ControllerOrder(PanierService panierService) {
        this.panierService = panierService;
    }

    @PostMapping("/shipping")
    public ResponseEntity<?> submitShippingForm(
            @Valid @RequestBody ShippingForm form,
            HttpSession session
    ) {
        session.setAttribute(SESSION_SHIPPING_FORM, form);

        Panier panier = (Panier) session.getAttribute(SESSION_PANIER);
        double poids = panierService.getPoidsTotal(panier);

        return ResponseEntity.ok(Map.of(
                "shippingForm", form,
                "poids", poids
        ));
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
