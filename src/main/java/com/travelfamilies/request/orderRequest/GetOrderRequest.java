package com.travelfamilies.request.orderRequest;

import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

public record GetOrderRequest(

        @JsonSerialize(using = ToStringSerializer.class)
        long hotelId,

        Integer status,

        int requestNum,

        int requestSize
) {
}
