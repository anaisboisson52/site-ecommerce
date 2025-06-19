package com.epsi.site_ecommerce.service;

import com.epsi.site_ecommerce.dto.Panier;
import com.epsi.site_ecommerce.dto.Product;
import com.epsi.site_ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PanierService {
    /**
     * Calcule le poids total du panier en kg (les poids des produits sont supposés en grammes).
     */
    public double getPoidsTotal(Panier panier) {
        if (panier == null || panier.getItems() == null) return 0.0;
        double poidsTotalGrammes = panier.getItems().stream()
                .mapToDouble(item -> {
                    Product product = item.getProduct();
                    if (product == null) return 0.0;
                    Double poids = product.getWeight();
                    return (poids != null ? poids : 0.0) * item.getQuantity();
                })
                .sum();
        return poidsTotalGrammes / 1000.0;
    }
}

