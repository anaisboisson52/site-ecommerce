package com.epsi.site_ecommerce.controller;

import com.epsi.site_ecommerce.entity.ProductEntity;
import com.epsi.site_ecommerce.service.ProductService;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

  /*  @GetMapping
    public ResponseEntity<Page<ProductEntity>> getProductsByPagination(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) int size) {

        return ResponseEntity.ok(productService.getAllProductsByPagination(page, size));
    }*/
}
