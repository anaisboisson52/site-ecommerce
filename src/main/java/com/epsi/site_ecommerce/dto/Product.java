package com.epsi.site_ecommerce.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

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

    public Product(String id, String name, String descriptionShort, String descriptionLong, int price, List<String> images, int weight, String category, int stock, String currency) {
        this.id = id;
        this.name = name;
        this.descriptionShort = descriptionShort;
        this.descriptionLong = descriptionLong;
        this.price = price;
        this.images = images;
        this.weight = weight;
        this.category = category;
        this.stock = stock;
        this.currency = currency;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescriptionShort() { return descriptionShort; }
    public void setDescriptionShort(String descriptionShort) { this.descriptionShort = descriptionShort; }

    public String getDescriptionLong() { return descriptionLong; }
    public void setDescriptionLong(String descriptionLong) { this.descriptionLong = descriptionLong; }

    public int getPrice() { return price; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public int getStock() { return stock; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}
