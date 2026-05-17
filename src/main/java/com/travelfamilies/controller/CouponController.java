package com.travelfamilies.controller;

import com.travelfamilies.pojo.Coupon;
import com.travelfamilies.request.couponRequest.GetCouponRequest;
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

    /** 
     * 抢购优惠券
     * 
     * @param couponId 优惠券主键 ID 
     * @param httpServletRequest HTTP 请求对象 
     * @return 优惠券详情 
     */ 
    @GetMapping
    public Result<?> getCoupon(@RequestParam String couponId, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return couponService.getCoupon(Long.parseLong(couponId), userId);
    }

    /** 
     * 添加优惠券 
     * 
     * @param coupon 优惠券对象 
     * @return 操作结果 
     */ 
    @PostMapping
    public Result<?> addCoupon(@RequestBody Coupon coupon) {

        return couponService.addCoupon(coupon);
    }


    /** 
     *  系统管理员or酒店管理员得到优惠券列表
     * 
     * @param id 酒店主键 ID（可选） 
     * @param type 优惠券类型 
     * @return 优惠券列表 
     */ 
    @GetMapping("/list")
    public Result<?> getCouponByHotelId(@RequestParam(required = false) String id, @RequestParam int type) {

        long hotelId = (id != null && !id.isEmpty()) ? Long.parseLong(id) : 0;
        return couponService.getCouponByRole(hotelId, type);
    }

    /** 
     * 更新优惠券状态 
     * 
     * @param couponId 优惠券主键 ID 
     * @return 操作结果 
     */ 
    @PutMapping("/{couponId}")
    public Result<?> updateStatus(@PathVariable String couponId) {


        return couponService.updateStatus(Long.parseLong(couponId));
    }

    /** 
     * 分页查询用户获得优惠券情况
     * 
     * @param getCouponRequest 查询优惠券请求对象 
     * @param httpServletRequest HTTP 请求对象 
     * @return 优惠券列表 
     */ 
    @PostMapping("/coupons")
    public Result<?> getCoupons(@RequestBody GetCouponRequest getCouponRequest, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return couponService.getCoupons(getCouponRequest, userId);
    }
}
