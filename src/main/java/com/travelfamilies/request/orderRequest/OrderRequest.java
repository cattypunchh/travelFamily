package com.travelfamilies.request.orderRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long hotelId;
    private int roomId;
    private String checkInDate;
    private String checkOutDate;
    @JsonSerialize(using = ToStringSerializer.class)
    private long couponId;
    private BigDecimal totalPrice;
}
