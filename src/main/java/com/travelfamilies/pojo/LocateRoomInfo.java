package com.travelfamilies.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocateRoomInfo {

    private int id;

    private String hotelId;

    private String hotelName;

    private String roomName;

    private String bedType;

    private String startTime;

    private String endTime;

    private BigDecimal originalPrice;

    private BigDecimal totalPrice;

    private BigDecimal discountAmount;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long couponId;

    private UserCouponVO userCoupons;
}
