package com.travelfamilies.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long couponId;

    private String title;

    private Integer scopeType;

    private String scopeId;

    private BigDecimal threshold;

    private BigDecimal discountAmount;

    private Integer stock;

    private Integer maxPerUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
