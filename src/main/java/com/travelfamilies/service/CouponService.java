package com.travelfamilies.service;

import com.travelfamilies.pojo.Coupon;
import com.travelfamilies.request.couponRequest.GetCouponRequest;
import com.travelfamilies.response.Result;

public interface CouponService {

    Result<?> getCoupon(long couponId, long userId);

    Result<?> addCoupon(Coupon coupon);

    Result<?> getCouponByRole(long hotelId, int type);

    Result<?> updateStatus(long couponId);

    Result<?> getCoupons(GetCouponRequest getCouponRequest, long userId);
}
