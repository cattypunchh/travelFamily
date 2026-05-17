package com.travelfamilies.request.orderRequest;

import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

public record SurePayRequest(

        @JsonSerialize(using = ToStringSerializer.class)
        long orderId,

        int resultPay,

        int type
) {
}
