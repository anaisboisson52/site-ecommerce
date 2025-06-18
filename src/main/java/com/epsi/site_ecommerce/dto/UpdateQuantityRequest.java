package com.epsi.site_ecommerce.dto;

public class UpdateQuantityRequest {
    private String productId;
    private int quantity;
    private int stock;

    public UpdateQuantityRequest() {}

    public UpdateQuantityRequest(String productId, int quantity, int stock) {
        this.productId = productId;
        this.quantity = quantity;
        this.stock = stock;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
