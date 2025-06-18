package com.epsi.site_ecommerce.dto;

import jakarta.validation.constraints.*;

public record ShippingForm(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String address,
        @NotBlank String city,
        @NotBlank String postalCode,
        @NotBlank String country,
        @Email String email,
        @Pattern(regexp="^\\+?[0-9 .-]{7,15}$", message="Téléphone invalide") String phone
) {}
