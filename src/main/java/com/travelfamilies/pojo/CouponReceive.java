package com.travelfamilies.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponReceive {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long couponId;

    private String title;

    private Integer scopeType;

    private Long scopeId;

    private BigDecimal threshold;

    private BigDecimal discountAmount;

    private Integer stock;

    private Integer maxPerUser;

    private LocalDateTime endTime;

    private Integer status;

    private Integer isReceived;
}
