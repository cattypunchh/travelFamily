package com.travelfamilies.mapper;

import com.travelfamilies.pojo.CheckInRecord;
import com.travelfamilies.pojo.HotelOrder;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO hotel_order (" +
            "id, user_id, hotel_id, room_id, check_in_date, check_out_date, " +
            "total_days, original_amount, coupon_id, coupon_amount, pay_amount, status, create_time" +
            ") VALUES (" +
            "#{id}, #{userId}, #{hotelId}, #{roomId}, #{checkInDate}, #{checkOutDate}, " +
            "#{totalDays}, #{originalAmount}, #{couponId}, #{couponAmount}, #{payAmount}, #{status}, NOW()" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int createOrder(HotelOrder hotelOrder);

    @Update("update hotel_order set status=#{resultPay} where id=#{orderId}")
    void updateStatus(long orderId, int resultPay);

    @Select("select * from hotel_order where id=#{orderId}")
    HotelOrder getOrder(long orderId);

    List<HotelOrder> getOrderSort(@Param("userId") Long userId,@Param("category") int category);

    @Select("select * from hotel_order where user_id=#{guestId} and status=1")
    List<HotelOrder> getOrderByGuestId(long guestId);

    @Select("select check_in_date from hotel_order where id=#{orderId}")
    String getCheckInTime(long orderId);


    @Select("select user_id from hotel_order where id=#{orderId}")
    long getGuestId(long orderId);

    @Insert("INSERT INTO check_in_record (" +
            "id,order_id, room_number, id_card, customer_id, " +
            "expect_check_in, expect_check_out, actual_check_in, " +
            "is_early_checkout, refund_amount, operator_id, create_time" +
            ") VALUES (" +
            "#{id},#{orderId}, #{roomNumber}, #{idCard}, #{customerId}, " +
            "#{expectCheckIn}, #{expectCheckOut}, #{actualCheckIn}, " +
            "#{isEarlyCheckout}, #{refundAmount}, #{operatorId},#{createTime}" +
            ")")
    int createCheckIn(CheckInRecord checkInRecord);

    @Select("select * from check_in_record where id=#{recordId}")
    CheckInRecord getRecord(long recordId);

    @Update("update  check_in_record set actual_check_out=#{expectTime},is_early_checkout=#{earlyCheckOut}," +
            "refund_amount=#{refundPrice},operator_id=#{userId} where id=#{recordId}")
    int updateRecordStatus(String expectTime, int earlyCheckOut, BigDecimal refundPrice, Long userId,long recordId);

    @Update("update hotel_order set pay_time=now() where id=#{orderId}")
    void updatePayTime(long orderId);
}
