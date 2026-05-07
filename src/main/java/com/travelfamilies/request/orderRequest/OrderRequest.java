package com.travelfamilies.request.orderRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private Long hotelId;
    private int roomId;
    private String checkInDate;
    private String checkOutDate;
    private long couponId;
    private BigDecimal totalPrice;
}
