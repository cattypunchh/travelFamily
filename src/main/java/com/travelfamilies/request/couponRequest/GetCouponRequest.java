package com.travelfamilies.request.couponRequest;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCouponRequest {

    private int type;
    private int status;
    private int pageNum;
    private int pageSize;
}
