package com.epsi.site_ecommerce.service;

import com.epsi.site_ecommerce.dto.Panier;
import com.epsi.site_ecommerce.dto.Product;
import com.epsi.site_ecommerce.dto.CartItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final ProductService productService;

    public CartService(ProductService productService) {
        this.productService = productService;
    }

    public void addProductToCart(Panier panier, Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }

        CartItem item = panier.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        int nouvelleQuantite = quantity;
        if (item != null) {
            nouvelleQuantite = item.getQuantity() + quantity;
        }

        if (nouvelleQuantite > product.getStock()) {
            throw new IllegalArgumentException("Stock insuffisant : demandé " + nouvelleQuantite + ", disponible " + product.getStock());
        }

        if (item != null) {
            item.setQuantity(nouvelleQuantite);
        } else {
            panier.addProduct(product, quantity);
        }
    }

    public void updateProductQuantity(Panier panier, String productId, int quantity, int stock) {
        if (quantity < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }
        if (quantity > stock) {
            throw new IllegalArgumentException("Stock insuffisant : demandé " + quantity + ", disponible " + stock);
        }

        CartItem item = panier.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé dans le panier"));

        item.setQuantity(quantity);
    }

    public void removeProductFromCart(Panier panier, String productId) {
        panier.removeProduct(productId);
    }

    public void consumeCartStock(Panier panier) {
        for (CartItem item : panier.getItems()) {
            Product upToDate = productService.getProductById(item.getProduct().getId());
            if (upToDate != null) {
                int nouveauStock = upToDate.getStock() - item.getQuantity();
                if (nouveauStock < 0) throw new IllegalStateException("Stock négatif détecté !");
                productService.updateProductStock(item.getProduct().getId(), nouveauStock);
            }
        }
    }

    public List<String> validateCart(Panier panier) {
        List<String> errors = new ArrayList<>();

        for (CartItem item : panier.getItems()) {
            Product productUpToDate = productService.getProductById(item.getProduct().getId());
            if (productUpToDate == null) {
                errors.add("Produit supprimé : " + item.getProduct().getName());
            } else if (productUpToDate.getStock() < item.getQuantity()) {
                errors.add("Stock insuffisant pour " + productUpToDate.getName() +
                        " (disponible : " + productUpToDate.getStock() + ", demandé : " + item.getQuantity() + ")");
            }
        }

        return errors;
    }
}
