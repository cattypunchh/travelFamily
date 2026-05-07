package com.travelfamilies.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInRecord {

    private Long id;
    private Long orderId;
    private String roomNumber;
    private String idCard;
    private Long customerId;
    private String expectCheckIn;
    private String expectCheckOut;
    private String actualCheckIn;
    private String actualCheckOut;
    private Integer isEarlyCheckout;
    private BigDecimal refundAmount;
    private Long operatorId;
    private String createTime;
}
