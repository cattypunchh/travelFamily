package com.travelfamilies.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCouponVO {

    private int id;
    private Long couponId;
    private Long userId;
    private String title;
    private String scopeType;
    private BigDecimal threshold;
    private BigDecimal discountAmount;
    private int userNum;
    private String endTime;
    private BigDecimal endPrice;



}
