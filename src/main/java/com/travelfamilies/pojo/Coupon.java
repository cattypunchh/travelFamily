package com.travelfamilies.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    private Long couponId;

    private String title;

    private Integer scopeType;

    private Integer scopeId;

    private BigDecimal threshold;

    private BigDecimal discountAmount;

    private Integer stock;

    private Integer maxPerUser;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
