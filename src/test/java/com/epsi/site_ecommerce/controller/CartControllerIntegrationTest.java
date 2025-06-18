package com.epsi.site_ecommerce.controller;

import com.epsi.site_ecommerce.dto.AddToCartRequest;
import com.epsi.site_ecommerce.dto.Product;
import com.epsi.site_ecommerce.dto.UpdateQuantityRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Product buildProduct(String id, String name, int price, int stock) {
        return new Product(
                id,
                name,
                "Desc courte",
                "Desc longue",
                price,
                List.of("https://image.com/" + id + ".jpg"),
                200,
                "Catégorie",
                stock,
                "EUR"
        );
    }

    @BeforeEach
    void setUp() throws Exception {
        // Vide le panier avant chaque test
        mockMvc.perform(delete("/api/cart/clear"));
    }

    @Test
    void addProductToCart_shouldAddAndReturnCart() throws Exception {
        Product p = buildProduct("1", "Produit Test", 12, 10);
        AddToCartRequest request = new AddToCartRequest(p, 3);

        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].product.id").value("1"))
                .andExpect(jsonPath("$.items[0].quantity").value(3));
    }

    @Test
    void addProductToCart_shouldAddEvenIfStockInsufficientInitially() throws Exception {
        Product p = buildProduct("2", "Produit Limite", 20, 2);
        AddToCartRequest request = new AddToCartRequest(p, 5); // stock insuffisant

        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].quantity").value(5));
    }

    @Test
    void updateProductQuantity_shouldUpdateAndReturnCart() throws Exception {
        Product p = buildProduct("1", "Produit Test", 12, 10);
        AddToCartRequest addRequest = new AddToCartRequest(p, 2);
        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        UpdateQuantityRequest updateRequest = new UpdateQuantityRequest("1", 7, 10);

        mockMvc.perform(put("/api/cart/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(7));
    }

    @Test
    void updateProductQuantity_shouldRejectIfStockInsufficient() throws Exception {
        Product p = buildProduct("3", "Produit Faible Stock", 15, 2);
        AddToCartRequest addRequest = new AddToCartRequest(p, 1);
        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        UpdateQuantityRequest updateRequest = new UpdateQuantityRequest("3", 5, 2);

        mockMvc.perform(put("/api/cart/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Stock insuffisant")));
    }

    @Test
    void removeProduct_shouldRemoveItemFromCart() throws Exception {
        Product p = buildProduct("4", "Produit à Supprimer", 10, 10);
        AddToCartRequest addRequest = new AddToCartRequest(p, 2);
        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/cart/remove/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void clearCart_shouldEmptyCart() throws Exception {
        Product p = buildProduct("5", "Produit Test", 10, 10);
        AddToCartRequest addRequest = new AddToCartRequest(p, 2);
        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/cart/clear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void getCart_shouldReturnAllItems() throws Exception {
        Product p1 = buildProduct("1", "Produit A", 10, 10);
        Product p2 = buildProduct("2", "Produit B", 15, 8);
        AddToCartRequest req1 = new AddToCartRequest(p1, 2);
        AddToCartRequest req2 = new AddToCartRequest(p2, 3);

        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[?(@.product.id=='1')].quantity", hasItem(2)))
                .andExpect(jsonPath("$.items[?(@.product.id=='2')].quantity", hasItem(3)));
    }

    @Test
    void validateCart_shouldReturnNoErrorsIfStockOk() throws Exception {
        Product p = buildProduct("1", "Produit OK", 10, 10);
        AddToCartRequest request = new AddToCartRequest(p, 2);

        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/cart/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.errors", hasSize(0)));
    }

    @Test
    void validateCart_shouldReturnErrorsIfStockInsufficient() throws Exception {
        Product p1 = buildProduct("1", "Produit A", 10, 1);  // stock faible
        Product p2 = buildProduct("2", "Produit B", 10, 5);

        AddToCartRequest r1 = new AddToCartRequest(p1, 3); // Demande > stock
        AddToCartRequest r2 = new AddToCartRequest(p2, 2);

        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(r1)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(r2)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/cart/validate"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.errors", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.errors[0]", containsString("Stock insuffisant")));
    }
}
