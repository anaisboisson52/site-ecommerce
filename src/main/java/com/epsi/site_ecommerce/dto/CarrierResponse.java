package com.epsi.site_ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CarrierResponse(
        String id,
        String name,
        String service_type,
        String area_served,
        double average_rating,
        @JsonProperty("max-weight") double maxWeight,
        String contact_email,
        String phone,
        String tracking_url_template,
        List<String> features
) {}
