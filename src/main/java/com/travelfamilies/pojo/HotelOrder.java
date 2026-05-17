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
public class HotelOrder {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @JsonSerialize(using = ToStringSerializer.class)
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

    private String hotelName;

    private String roomName;
}
