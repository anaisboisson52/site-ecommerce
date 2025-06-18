package com.epsi.site_ecommerce.dto;

import java.io.Serializable;
import java.util.*;

public class Panier implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, CartItem> items = new HashMap<>();

    public void addProduct(Product product, int quantity) {
        if (items.containsKey(product.getId())) {
            CartItem item = items.get(product.getId());
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            items.put(product.getId(), new CartItem(product, quantity));
        }
    }

    public void setProductQuantity(String productId, int quantity) {
        if (items.containsKey(productId)) {
            items.get(productId).setQuantity(quantity);
        }
    }

    public void removeProduct(String productId) {
        items.remove(productId);
    }

    public Collection<CartItem> getItems() {
        return items.values();
    }

    public double getTotal() {
        return items.values().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }
}
