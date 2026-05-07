package com.travelfamilies.controller;

import com.travelfamilies.pojo.Coupon;
import com.travelfamilies.response.Result;
import com.travelfamilies.service.CouponService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public Result<?> getCoupon(@RequestParam long coupon_id, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return couponService.getCoupon(coupon_id, userId);
    }

    @PostMapping
    public Result<?> addCoupon(@RequestBody Coupon coupon) {

        return couponService.addCoupon(coupon);
    }
}
