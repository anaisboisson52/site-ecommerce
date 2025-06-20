package com.epsi.site_ecommerce.controller;

import com.epsi.site_ecommerce.dto.Panier;
import com.epsi.site_ecommerce.dto.ShippingForm;
import com.epsi.site_ecommerce.service.CartService;
import com.epsi.site_ecommerce.service.PanierService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class ControllerOrder {

    private static final String SESSION_SHIPPING_FORM = "shippingForm";
    private static final String SESSION_PANIER = "panier";

    private final PanierService panierService;
    private final CartService cartService;

    public ControllerOrder(PanierService panierService, CartService cartService) {
        this.panierService = panierService;
        this.cartService = cartService;
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

    @GetMapping("/shipping")
    public ResponseEntity<?> getShippingForm(HttpSession session) {
        ShippingForm form = (ShippingForm) session.getAttribute(SESSION_SHIPPING_FORM);

        if (form == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(form);
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmOrder(HttpSession session) {
        Panier panier = (Panier) session.getAttribute(SESSION_PANIER);
        if (panier == null || panier.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("Le panier est vide");
        }
        List<String> errors = cartService.validateCart(panier);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        try {
            cartService.consumeCartStock(panier);
            session.removeAttribute(SESSION_PANIER);
            return ResponseEntity.ok(Map.of("message", "Commande confirmée"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur");
        }
    }
}
