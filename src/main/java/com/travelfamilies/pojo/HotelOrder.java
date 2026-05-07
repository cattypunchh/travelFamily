package com.travelfamilies.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelOrder {

    private Long id;

    private Long userId;

    private Long hotelId;

    private Integer roomId;

    private String checkInDate;

    private String checkOutDate;

    private Integer totalDays;

    private BigDecimal originalAmount;

    private Long couponId;

    private BigDecimal couponAmount;

    private BigDecimal payAmount;

    private Integer status;

    private String createTime;

    private LocalDateTime payTime;
}
