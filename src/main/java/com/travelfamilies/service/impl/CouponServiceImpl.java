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
        String stockKey = RedisConstant.COUPON_STOCK + couponId;
        String limitKey = RedisConstant.COUPON_LIMIT + couponId;

        // 1. 检查每人限制
        String limitStr = stringRedisTemplate.opsForValue().get(limitKey);
        int everLimit = limitStr != null ? Integer.parseInt(limitStr) : Integer.MAX_VALUE;

        // 2. 原子递增领取次数，检查是否超限
        Long getCount = stringRedisTemplate.opsForValue().increment(getKey);
        if (getCount != null && getCount > everLimit) {
            stringRedisTemplate.opsForValue().decrement(getKey);
            return Result.failed("每人限制领取" + everLimit + "张，你已达上限");
        }

        // 3. 原子递减库存（先扣 Redis，避免超卖）
        Long remainStock = stringRedisTemplate.opsForValue().decrement(stockKey);
        if (remainStock == null || remainStock < 0) {
            // 库存不足，回滚 Redis
            stringRedisTemplate.opsForValue().increment(stockKey);
            stringRedisTemplate.opsForValue().decrement(getKey);
            return Result.failed("该优惠券已被抢光");
        }

        // 4. 扣减数据库库存
        int resultDecr = couponMapper.decrStock(couponId);
        if (resultDecr <= 0) {
            // DB 扣减失败，回滚 Redis
            stringRedisTemplate.opsForValue().increment(stockKey);
            stringRedisTemplate.opsForValue().decrement(getKey);
            return Result.failed("该优惠券已被抢光");
        }

        // 5. 创建领取记录
        int result = couponMapper.addCouponOrder(couponId, userId);
        if (result <= 0) {
            // 领取失败，回滚 Redis 和 DB
            stringRedisTemplate.opsForValue().increment(stockKey);
            stringRedisTemplate.opsForValue().decrement(getKey);
            couponMapper.incrementStock(couponId);
            throw new RuntimeException("领取优惠券失败");
        }

        return Result.success();
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
