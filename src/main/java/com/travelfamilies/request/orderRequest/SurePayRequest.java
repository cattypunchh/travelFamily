package com.travelfamilies.request.orderRequest;

public record SurePayRequest(
        long orderId,
        int resultPay,
        int type
) {
}
