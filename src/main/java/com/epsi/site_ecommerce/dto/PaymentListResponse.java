package com.epsi.site_ecommerce.dto;

import java.util.List;

public class PaymentListResponse {
    private List<PaymentResponse> payments;
    private double total;

    public PaymentListResponse(List<PaymentResponse> payments, double total) {
        this.payments = payments;
        this.total = total;
    }

    public List<PaymentResponse> getPayments() {
        return payments;
    }

    public double getTotal() {
        return total;
    }
}
