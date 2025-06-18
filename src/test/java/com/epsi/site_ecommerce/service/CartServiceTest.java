package com.epsi.site_ecommerce.service;

import com.epsi.site_ecommerce.dto.CartItem;
import com.epsi.site_ecommerce.dto.Panier;
import com.epsi.site_ecommerce.dto.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    private CartService cartService;
    private ProductService productService;
    private Panier panier;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        cartService = new CartService(productService);
        panier = new Panier();
    }

    private Product buildProduct(String id, String name, int price, int stock) {
        return new Product(
                id, name, "Desc courte", "Desc longue", price,
                List.of("https://img/" + id + ".jpg"),
                200, "Catégorie", stock, "EUR"
        );
    }

    @Test
    void validateCart_shouldReturnNoErrorsIfAllProductsInStock() {
        Product p1 = buildProduct("1", "Produit A", 10, 5);
        Product p2 = buildProduct("2", "Produit B", 7, 10);

        cartService.addProductToCart(panier, p1, 3);
        cartService.addProductToCart(panier, p2, 2);

        when(productService.getProductById("1")).thenReturn(p1);
        when(productService.getProductById("2")).thenReturn(p2);

        List<String> errors = cartService.validateCart(panier);
        assertThat(errors).isEmpty();
    }

    @Test
    void validateCart_shouldReturnErrorIfStockInsufficient() {
        Product p1 = buildProduct("1", "Produit A", 10, 5);
        cartService.addProductToCart(panier, p1, 4);

        Product p1Updated = buildProduct("1", "Produit A", 10, 3);
        when(productService.getProductById("1")).thenReturn(p1Updated);

        List<String> errors = cartService.validateCart(panier);
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0)).contains("Stock insuffisant");
    }

    @Test
    void validateCart_shouldReturnErrorIfProductNotFound() {
        Product p1 = buildProduct("1", "Produit A", 10, 5);
        cartService.addProductToCart(panier, p1, 2);

        when(productService.getProductById("1")).thenReturn(null);

        List<String> errors = cartService.validateCart(panier);
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0)).contains("Produit supprimé");
    }

    @Test
    void validateCart_shouldReturnAllErrorsForMultipleIssues() {
        Product p1 = buildProduct("1", "Produit A", 10, 5);
        Product p2 = buildProduct("2", "Produit B", 7, 8);

        cartService.addProductToCart(panier, p1, 4);
        cartService.addProductToCart(panier, p2, 2);

        when(productService.getProductById("1")).thenReturn(buildProduct("1", "Produit A", 10, 3));
        when(productService.getProductById("2")).thenReturn(null);

        List<String> errors = cartService.validateCart(panier);
        assertThat(errors).hasSize(2);
        assertThat(errors.get(0)).contains("Stock insuffisant");
        assertThat(errors.get(1)).contains("Produit supprimé");
    }
}
