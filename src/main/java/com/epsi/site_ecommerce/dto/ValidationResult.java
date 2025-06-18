package com.epsi.site_ecommerce.dto;

import java.util.List;

public record ValidationResult(
        boolean valid,
        Panier panier,
        List<String> errors
) {}
