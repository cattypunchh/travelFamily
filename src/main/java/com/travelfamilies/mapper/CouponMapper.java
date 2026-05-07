package com.travelfamilies.mapper;

import com.travelfamilies.pojo.Coupon;
import com.travelfamilies.pojo.UserCouponVO;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CouponMapper {

    @Insert("insert into  user_coupons (user_id,coupon_id) values (#{userId},#{couponId})")
    int addCouponOrder(Long couponId, long userId);

    @Insert("insert into coupons(coupon_id,title, scope_type, scope_id, threshold, discount_amount," +
            "stock, max_per_user, start_time, end_time, status) " +
            "values(#{couponId},#{title}, #{scopeType}, #{scopeId}, #{threshold}, #{discountAmount}," +
            "#{stock}, #{maxPerUser}, #{startTime}, #{endTime}, #{status})")
    int addCoupon(Coupon coupon);

    @Update("update coupons set stock=stock-1 where coupon_id=#{couponId} and stock>0")
    int decrStock(long couponId);

    @Select("select * from coupons where  scope_type=2 and scope_id=#{hotelId} and status=1")
    List<Coupon> getCouponByHotelId(Long hotelId);

    List<UserCouponVO> getCouponByUser(@Param("hotelId") Long hotelId, @Param("userId") Long userId,
                                       @Param("now") String now, @Param("totalPrice") BigDecimal totalPrice);

    @Select("select discount_amount from coupons where coupon_id=#{couponId}")
    BigDecimal getDiscount(Long couponId);

    @Update("update user_coupons set status=#{status} where user_id=#{userId} and coupon_id=#{couponId}")
    int updateStatues(int status,Long userId, Long couponId);
}
