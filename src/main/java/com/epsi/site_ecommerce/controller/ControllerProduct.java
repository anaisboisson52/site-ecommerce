package com.epsi.site_ecommerce.controller;

import com.epsi.site_ecommerce.dto.ProductDTO;
import com.epsi.site_ecommerce.entity.ProductEntity;
import com.epsi.site_ecommerce.service.ProductService;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/product")
@Validated
public class ControllerProduct {

    private final ProductService productService;

    public ControllerProduct(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping
    public ResponseEntity<List<ProductEntity>> getAllProduct() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<ProductEntity>> getProductsByPagination(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) int size) {
        return ResponseEntity.ok(productService.getAllProductsByPagination(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductEntity> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }

    @PostMapping("/products")
    public List<ProductEntity> createProduct(@RequestBody List<ProductEntity> newProducts) {
        List<ProductEntity> savedProduct = productService.saveProducts(newProducts);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED).getBody();
    }

}
