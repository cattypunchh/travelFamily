package com.travelfamilies.request.spotRequest;

import java.math.BigDecimal;

public record UpdateDetailRequest(
        BigDecimal price,
        String openTime,
        String description,
        String imageUrls) {
}
