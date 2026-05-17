package com.travelfamilies.request.spotRequest;

import java.math.BigDecimal;
import java.util.List;

public record UpdateDetailRequest(
        BigDecimal price,
        String openTime,
        String description,
        List<String> imageUrls) {
}
