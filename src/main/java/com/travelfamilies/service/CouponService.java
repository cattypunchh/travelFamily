package com.travelfamilies.service;

import com.travelfamilies.pojo.Coupon;
import com.travelfamilies.response.Result;

public interface CouponService {

    Result<?> getCoupon(long couponId, long userId);

    Result<?> addCoupon(Coupon coupon);
}
