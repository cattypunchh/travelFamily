package com.travelfamilies.request.spotRequest;

public record QuerySpotRequest(
        String key,
        String value,
        String keyword
) {
}
