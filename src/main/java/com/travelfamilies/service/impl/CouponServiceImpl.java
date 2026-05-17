package com.travelfamilies.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.travelfamilies.mapper.CouponMapper;
import com.travelfamilies.pojo.Coupon;
import com.travelfamilies.pojo.CouponReceive;
import com.travelfamilies.request.couponRequest.GetCouponRequest;
import com.travelfamilies.response.Result;
import com.travelfamilies.service.CouponService;
import com.travelfamilies.tools.RedisConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> getCoupon(long couponId, long userId) {

        String getKey = RedisConstant.COUPON_GET_DETAIL + couponId + ":" + userId;
        String decrKey = RedisConstant.COUPON_STOCK + couponId;

        int everLimit = Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(RedisConstant.COUPON_LIMIT + couponId)));

        int getCount = Math.toIntExact(stringRedisTemplate.opsForValue().increment(getKey));
        if (getCount > everLimit) {
            stringRedisTemplate.opsForValue().decrement(getKey);
            return Result.failed("每人限制领取" + everLimit + "张，你已达上限");
        }

        if (Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(decrKey))) <= 0) {

            return Result.failed("该优惠券已被抢光");
        }

        int resultDecr = couponMapper.decrStock(couponId);
        if (resultDecr > 0) {

            stringRedisTemplate.opsForValue().decrement(decrKey);
            int result = couponMapper.addCouponOrder(couponId, userId);
            if (result > 0) {
                return Result.success();
            } else {
                stringRedisTemplate.opsForValue().decrement(getKey);

                throw new RuntimeException("领取优惠券失败");
            }
        } else {
            return Result.failed("该优惠券已被抢光");
        }

    }

    @Override
    public Result<?> addCoupon(Coupon coupon) {

        long couponId = cn.hutool.core.util.IdUtil.getSnowflakeNextId();
        coupon.setCouponId(couponId);

        int resultAdd = couponMapper.addCoupon(coupon, Long.parseLong(coupon.getScopeId()));
        if (resultAdd == 0) {

            return Result.failed("添加优惠券失败，请重试");
        }
        stringRedisTemplate.opsForValue().set(RedisConstant.COUPON_STOCK + couponId, String.valueOf(coupon.getStock()));
        stringRedisTemplate.opsForValue().set(RedisConstant.COUPON_DISCOUNT + couponId, String.valueOf(coupon.getDiscountAmount()));
        stringRedisTemplate.opsForValue().set(RedisConstant.COUPON_LIMIT + couponId, String.valueOf(coupon.getMaxPerUser()));
        return Result.success(couponId);
    }

    @Override
    public Result<?> getCouponByRole(long hotelId, int type) {
        return Result.success(type == 1 ?
                couponMapper.getCouponGeneral() :
                couponMapper.getCouponByHotelIdManage(hotelId)
        );
    }

    @Override
    public Result<?> updateStatus(long couponId) {

        return couponMapper.updateStatuesCoupon(couponId) > 0 ?
                Result.success()
                : Result.failed("更改状态失败");
    }

    @Override
    public Result<?> getCoupons(GetCouponRequest getCouponRequest, long userId) {

        PageHelper.startPage(getCouponRequest.getPageNum(), getCouponRequest.getPageSize());

        List<CouponReceive> coupons = couponMapper.getCoupons(getCouponRequest, userId);

        PageInfo<CouponReceive> pageInfo = new PageInfo<>(coupons);

        return Result.success(pageInfo);
    }
}
