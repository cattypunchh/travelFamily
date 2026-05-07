package com.travelfamilies.service.impl;

import com.travelfamilies.config.RabbitConfig;
import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.mapper.CouponMapper;
import com.travelfamilies.mapper.HotelMapper;
import com.travelfamilies.mapper.OrderMapper;
import com.travelfamilies.mapper.UserMapper;
import com.travelfamilies.pojo.CheckInRecord;
import com.travelfamilies.pojo.HotelOrder;
import com.travelfamilies.pojo.LocateRoomInfo;
import com.travelfamilies.pojo.UserCouponVO;
import com.travelfamilies.request.hotelRequest.LocateRoomRequest;
import com.travelfamilies.request.orderRequest.CheckInRequest;
import com.travelfamilies.request.orderRequest.OrderRequest;
import com.travelfamilies.request.orderRequest.SurePayRequest;
import com.travelfamilies.response.GetRoomInfoOrderResponse;
import com.travelfamilies.response.Result;
import com.travelfamilies.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final HotelMapper hotelMapper;
    private final CouponMapper couponMapper;
    private final OrderMapper orderMapper;
    private final RabbitTemplate rabbitTemplate;
    private final UserMapper userMapper;

    @Override
    public Result<?> getRoomDetail(LocateRoomRequest locateRoomRequest, Long userId) {

        int roomId = locateRoomRequest.roomId();
        GetRoomInfoOrderResponse roomInfo = hotelMapper.getRoomDetail(roomId);
        String hotelName = hotelMapper.getHotelByID(locateRoomRequest.hotelId());
        LocateRoomInfo locateRoomInfo = new LocateRoomInfo();
        BeanUtils.copyProperties(roomInfo, locateRoomInfo);
        locateRoomInfo.setHotelName(hotelName);

        String localDays = LocalDate.parse(locateRoomRequest.endTime()).minusDays(1).toString();
        BigDecimal totalPrice = hotelMapper.getTotalPrice(locateRoomRequest, localDays);
        BigDecimal bestPrice = totalPrice;
        locateRoomInfo.setOriginalPrice(totalPrice);
        locateRoomInfo.setTotalPrice(totalPrice);

        String now = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<UserCouponVO> userCoupons = couponMapper.getCouponByUser(locateRoomRequest.hotelId(), userId, now, totalPrice);
        Long couponId = null;
        for (UserCouponVO userCoupon : userCoupons) {

            BigDecimal discount = couponMapper.getDiscount(userCoupon.getCouponId());
            BigDecimal endPrice;
            if (discount.compareTo(BigDecimal.ZERO) > 0 && discount.compareTo(BigDecimal.ONE) < 0) {
                endPrice = totalPrice.multiply(discount);
            } else {
                endPrice = totalPrice.subtract(discount);
            }

            userCoupon.setEndPrice(endPrice);
            if (endPrice.compareTo(bestPrice) < 0) {
                bestPrice = endPrice;
                couponId = userCoupon.getCouponId();
            }
        }
        locateRoomInfo.setTotalPrice(bestPrice);
        locateRoomInfo.setCouponId(couponId);
        locateRoomInfo.setUserCoupons(userCoupons);
        return Result.success(locateRoomInfo);
    }

    @Override
    @Transactional
    public Result<?> submitOrder(OrderRequest orderRequest, Long userId) throws BusinessException {


        LocalDate start = LocalDate.parse(orderRequest.getCheckInDate());
        LocalDate end = LocalDate.parse(orderRequest.getCheckOutDate());
        String localDays = end.minusDays(1).toString();
        long days = ChronoUnit.DAYS.between(start, end);
        int resultRedStock = hotelMapper.reduceStockRoom(orderRequest.getRoomId(), orderRequest.getCheckInDate(), localDays);

        if (resultRedStock != days) {

            throw new BusinessException("部分房型库存为空，请重新选择");
        }
        HotelOrder hotelOrder = new HotelOrder();
        long id = cn.hutool.core.util.IdUtil.getSnowflakeNextId();
        hotelOrder.setId(id);
        hotelOrder.setUserId(userId);
        Long couponId = orderRequest.getCouponId();
        BigDecimal totalPrice = orderRequest.getTotalPrice();
        hotelOrder.setOriginalAmount(totalPrice);
        hotelOrder.setTotalDays((int) days);
        if (couponId != null) {

            hotelOrder.setCouponId(couponId);
            BigDecimal discount = couponMapper.getDiscount(couponId);
            hotelOrder.setCouponAmount(discount);
            if (discount.compareTo(BigDecimal.ZERO) > 0 && discount.compareTo(BigDecimal.ONE) < 0) {
                totalPrice = totalPrice.multiply(discount);
            } else {
                totalPrice = totalPrice.subtract(discount);
            }
        }
        hotelOrder.setPayAmount(totalPrice);
        BeanUtils.copyProperties(orderRequest, hotelOrder);

        hotelOrder.setStatus(0);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        hotelOrder.setCreateTime(now);


        if (orderMapper.createOrder(hotelOrder) > 0) {


            int result = couponMapper.updateStatues(1, userId, couponId);
            if (result <= 0) {

                couponMapper.updateStatues(0, userId, couponId);
                return Result.failed("使用该优惠券失败，请返回重新选择");
            }

            //rabbitTemplate.convertAndSend(RabbitConfig.ORDER_TTL_EXCHANGE, "ttl", id);


            return Result.success(hotelOrder.getId());

        } else {
            return Result.failed("订单创建失败，请点击提交按钮重试");
        }

    }

    @Override
    @Transactional
    public Result<?> surePay(SurePayRequest surePayRequest, Long userId) throws BusinessException {

        /* resultPay=1 pay 0 noPay 3 取消*/
        int resultPay = surePayRequest.resultPay();
        /*1 提交按钮之后的选择 2. 在未支付列表的选择*/
        int type = surePayRequest.type();
        long orderId = surePayRequest.orderId();

        if (type == 1) {

            if (resultPay == 1) {

                orderMapper.updateStatus(orderId, resultPay);
                orderMapper.updatePayTime(orderId);

                return Result.success("支付成功，已成功预约该酒店");
            } else {

                rabbitTemplate.convertAndSend(RabbitConfig.ORDER_TTL_EXCHANGE, "ttl", orderId);
                return Result.failed("请在15分钟之内支付该订单，超时预约取消");
            }
        } else {

            if (resultPay == 3) {

                orderMapper.updateStatus(orderId, resultPay);
                HotelOrder hotelOrder = orderMapper.getOrder(orderId);
                /*回滚库存+更改优惠券状态*/
                int resultCoupon = couponMapper.updateStatues(0, userId, hotelOrder.getCouponId());
                if (resultCoupon != 1) {

                    return Result.failed("更改优惠券状态失败");
                }

                String startTime = hotelOrder.getCheckInDate();
                LocalDate end = LocalDate.parse(hotelOrder.getCheckOutDate());
                LocalDate start = LocalDate.parse(startTime);
                long days = ChronoUnit.DAYS.between(start, end);
                String localDays = String.valueOf(end.minusDays(1));

                int result = hotelMapper.rollBackStock(hotelOrder.getRoomId(), startTime, localDays);

                if (result < days) {

                    throw new BusinessException("回滚库存失败");
                }

                return Result.failed("取消订单成功");

            } else {

                /*这时候要再次弹出来那个确定支付的订单*/
                if (resultPay == 1) {
                    orderMapper.updateStatus(orderId, resultPay);
                    return Result.success("支付成功，已成功预约该酒店");
                } else {

                    return Result.failed("请在15分钟之内支付该订单，超时预约取消");
                }
            }
        }

    }

    @Override
    public Result<?> getOrderSort(Long userId, Integer category) {

        if (userId == null) {

            return Result.failed("获取失败，请重试");
        }
        /*1.已支付 2.已入住 0.待支付 3，已取消，4 全部*/
        List<HotelOrder> unPayOrders = orderMapper.getOrderSort(userId, category);

        return unPayOrders == null ? Result.failed("没找到订单，请重试") : Result.success(unPayOrders);
    }

    /*接下开是酒店工作人员操作的接口*/
    @Override
    public Result<?> getOrderByGuest(String guestName) {

        /*查出客户id -> 查看他的已支付未入住订单 - 确认订单 - 核销 - 创建入住单 */

        Long guestId = userMapper.getUserByName(guestName);

        if (guestId==null) {

            return Result.failed("暂时无该客户，请核对名字重新输入");
        }

        List<HotelOrder> hotelOrders = orderMapper.getOrderByGuestId(guestId);


        return hotelOrders == null ? Result.success("未查询到该客户的订单，请重试") : Result.success(hotelOrders);
    }

    @Override
    public Result<?> checkIn(CheckInRequest checkInRequest, Long userId) {

        long orderId = checkInRequest.getOrderId();
        LocalDate fixCheckInTime = LocalDate.parse(orderMapper.getCheckInTime(orderId));
        LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        if (!now.isEqual(fixCheckInTime)) {

            return Result.failed("还未到入住时间，无法入住");
        }

        long guestId = orderMapper.getGuestId(orderId);
        long id = cn.hutool.core.util.IdUtil.getSnowflakeNextId();
        HotelOrder hotelOrder = orderMapper.getOrder(orderId);
        CheckInRecord checkInRecord = new CheckInRecord(id, orderId, checkInRequest.getRoomNumber(),
                checkInRequest.getIdCard(), guestId, hotelOrder.getCheckInDate(), hotelOrder.getCheckOutDate(),
                hotelOrder.getCheckInDate(), hotelOrder.getCheckOutDate(),
                0, new BigDecimal("0"), userId, String.valueOf(now));
        int result = orderMapper.createCheckIn(checkInRecord);

        if (result > 0) {
            orderMapper.updateStatus(orderId, 2);
            return Result.success(id);
        } else {
            return Result.failed("办理入住失败");
        }
    }

    @Override
    public Result<?> checkOut(long recordId, Long userId) {


        LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        CheckInRecord record = orderMapper.getRecord(recordId);

        LocalDate expectTime = LocalDate.parse(record.getExpectCheckOut());

        String actualCheckIn = record.getActualCheckIn();
        HotelOrder order = orderMapper.getOrder(record.getOrderId());
        System.out.println(order);
        BigDecimal refundPrice=new BigDecimal("0.00");
        if (now.isBefore(expectTime)) {

            long expectLocalDays = ChronoUnit.DAYS.between(
                    LocalDate.parse(record.getExpectCheckIn()),LocalDate.parse(record.getExpectCheckOut()));
            long actualLocalDays = ChronoUnit.DAYS.between(LocalDate.parse(actualCheckIn), now);

            refundPrice =order.getPayAmount().divide(new BigDecimal(expectLocalDays), 2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal((expectLocalDays-actualLocalDays)))
                    .setScale(2, RoundingMode.HALF_UP);

        }
        int earlyCheckOut=0;

        if(refundPrice.compareTo(BigDecimal.ZERO)!=0){

            earlyCheckOut=1;
            expectTime=now;
        }

        int resultRecord=orderMapper.updateRecordStatus(String.valueOf(expectTime),earlyCheckOut,refundPrice,userId,recordId);

        int result=hotelMapper.rollBackStock(order.getRoomId(), actualCheckIn,
                String.valueOf(record.getExpectCheckOut()));

        if(order.getStatus()==4){

            return Result.failed("已退房 不可重复退房");
        }

        if(resultRecord>0){

            orderMapper.updateStatus(order.getId(),4);
            return result>0 ?  Result.success("退房成功，库存回滚成功"):Result.failed("退房成功 但回滚库存失败，请注意");
        }else{

            return result>0 ? Result.success("退房失败但回滚库存成功"):Result.failed("退房失败,回滚库存失败，请注意");
        }
    }


}
