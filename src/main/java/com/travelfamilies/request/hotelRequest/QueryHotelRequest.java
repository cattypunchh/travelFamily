package com.travelfamilies.request.hotelRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record QueryHotelRequest(
        String key,
        String value,
        String keyword
) {
}
