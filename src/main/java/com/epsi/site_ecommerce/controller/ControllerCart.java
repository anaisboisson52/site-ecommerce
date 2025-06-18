package com.epsi.site_ecommerce.controller;

import com.epsi.site_ecommerce.dto.*;
import com.epsi.site_ecommerce.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class ControllerCart {

    private final CartService cartService;

    public ControllerCart(CartService cartService) {
        this.cartService = cartService;
    }

    private Panier getSessionPanier(HttpSession session) {
        Panier panier = (Panier) session.getAttribute("panier");
        if (panier == null) {
            panier = new Panier();
            session.setAttribute("panier", panier);
        }
        return panier;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProductToCart(@RequestBody AddToCartRequest request, HttpSession session) {
        try {
            Panier panier = getSessionPanier(session);
            cartService.addProductToCart(panier, request.getProduct(), request.getQuantity());
            session.setAttribute("panier", panier); // 🔥 MISE À JOUR SESSION
            return ResponseEntity.ok(panier);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProductQuantity(@RequestBody UpdateQuantityRequest request, HttpSession session) {
        try {
            Panier panier = getSessionPanier(session);
            cartService.updateProductQuantity(panier, request.getProductId(), request.getQuantity(), request.getStock());
            session.setAttribute("panier", panier); // 🔥 MISE À JOUR SESSION
            return ResponseEntity.ok(panier);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Panier> removeProduct(@PathVariable String productId, HttpSession session) {
        Panier panier = getSessionPanier(session);
        cartService.removeProductFromCart(panier, productId);
        session.setAttribute("panier", panier); // 🔥 MISE À JOUR SESSION
        return ResponseEntity.ok(panier);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Panier> clearCart(HttpSession session) {
        Panier panier = new Panier();
        session.setAttribute("panier", panier); // 🔥 RESET SESSION
        return ResponseEntity.ok(panier);
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidationResult> validateCartAndUpdateStock(HttpSession session) {
        Panier panier = getSessionPanier(session);
        List<String> errors = cartService.validateCart(panier);

        if (!errors.isEmpty()) {
            ValidationResult result = new ValidationResult(false, panier, errors);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }

        return ResponseEntity.ok(new ValidationResult(true, panier, List.of()));
    }


    @GetMapping
    public ResponseEntity<Panier> getCart(HttpSession session) {
        return ResponseEntity.ok(getSessionPanier(session));
    }
}
