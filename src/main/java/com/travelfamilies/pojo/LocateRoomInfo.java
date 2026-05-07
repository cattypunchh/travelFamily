package com.travelfamilies.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

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
    private Long couponId;
    private List<UserCouponVO> userCoupons;
}
