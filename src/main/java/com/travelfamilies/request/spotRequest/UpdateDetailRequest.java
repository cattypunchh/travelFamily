package com.travelfamilies.request.spotRequest;

import java.math.BigDecimal;

public record UpdateDetailRequest(int id, BigDecimal price, String open_time, String description, String image_urls) {
}
