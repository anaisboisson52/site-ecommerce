package com.epsi.site_ecommerce.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class ProductDTO {

    private String id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String name;

    @Size(max = 255, message = "La description courte ne doit pas dépasser 255 caractères")
    private String descriptionShort;

    private String descriptionLong;

    @Min(value = 0, message = "Le prix doit être positif")
    private int price;

    @NotBlank(message = "La devise est obligatoire")
    private String currency;

    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    private int stock;

    @NotBlank(message = "La catégorie est obligatoire")
    private String category;

    @Min(value = 0, message = "Le poids doit être positif")
    private int weight;

    @NotNull(message = "La liste d'images ne peut pas être null")
    private List<@NotBlank(message = "Chaque URL d'image doit être non vide") String> images;

}
