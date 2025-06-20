package com.epsi.site_ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PaymentResponse(
        String id,
        String order_id,
        String customer_id,
        String currency,
        String method,
        String status,
        String transaction_date,
        String processor_response,
        String note
) { }
