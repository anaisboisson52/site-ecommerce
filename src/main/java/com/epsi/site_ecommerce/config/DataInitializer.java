package com.epsi.site_ecommerce.config;

import com.epsi.site_ecommerce.entity.ProductEntity;
import com.epsi.site_ecommerce.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void initData() {
        ProductEntity product1 = new ProductEntity();
        product1.setId(UUID.randomUUID().toString());
        product1.setName("Chaussures de sport");
        product1.setDescriptionShort("Chaussures confortables pour le sport");
        product1.setDescriptionLong("Des chaussures légères, respirantes, idéales pour la course à pied ou le fitness.");
        product1.setPrice(5999); // En centimes pour précision
        product1.setCurrency("EUR");
        product1.setStock(50);
        product1.setCategory("Chaussures");
        product1.setWeight(850); // grammes
        product1.setImages(List.of("https://example.com/images/chaussure1.jpg", "https://example.com/images/chaussure2.jpg"));

        ProductEntity product2 = new ProductEntity();
        product2.setId(UUID.randomUUID().toString());
        product2.setName("T-shirt en coton");
        product2.setDescriptionShort("T-shirt 100% coton");
        product2.setDescriptionLong("T-shirt basique confortable et respirant, parfait pour l'été.");
        product2.setPrice(1999);
        product2.setCurrency("EUR");
        product2.setStock(150);
        product2.setCategory("Vêtements");
        product2.setWeight(200);
        product2.setImages(List.of("https://example.com/images/tshirt1.jpg"));

        ProductEntity product3 = new ProductEntity();
        product3.setId(UUID.randomUUID().toString());
        product3.setName("Sac à dos étanche");
        product3.setDescriptionShort("Sac pratique et résistant à l'eau");
        product3.setDescriptionLong("Sac à dos parfait pour les randonnées ou les déplacements urbains. Capacité : 25L.");
        product3.setPrice(4599);
        product3.setCurrency("EUR");
        product3.setStock(75);
        product3.setCategory("Accessoires");
        product3.setWeight(1200);
        product3.setImages(List.of("https://example.com/images/sac1.jpg", "https://example.com/images/sac2.jpg"));

        productRepository.saveAll(List.of(product1, product2, product3));
    }
}
