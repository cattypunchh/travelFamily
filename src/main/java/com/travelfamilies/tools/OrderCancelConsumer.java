package com.travelfamilies.tools;

import com.travelfamilies.config.RabbitConfig;
import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.mapper.CouponMapper;
import com.travelfamilies.mapper.HotelMapper;
import com.travelfamilies.mapper.OrderMapper;
import com.travelfamilies.pojo.HotelOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Configuration
@RequiredArgsConstructor
public class OrderCancelConsumer {

    private final OrderMapper orderMapper;
    private final HotelMapper  hotelMapper;
    private final CouponMapper couponMapper;

    @RabbitListener(queues = RabbitConfig.ORDER_CANCEL_QUEUE)
    @Transactional
    public void updateOrderStatus(long orderId) throws BusinessException {

        HotelOrder hotelOrder=orderMapper.getOrder(orderId);

        System.out.println(hotelOrder);
        if(hotelOrder.getStatus()==0){

            orderMapper.updateStatus(orderId,3);

            String startTime=hotelOrder.getCheckInDate();
            LocalDate end= LocalDate.parse(hotelOrder.getCheckOutDate());
            LocalDate start= LocalDate.parse(startTime);
            long days= ChronoUnit.DAYS.between(start, end);
            String localDays = String.valueOf(end.minusDays(1));
            int resultRoll=hotelMapper.rollBackStock(hotelOrder.getRoomId(),startTime,localDays);

            long couponId=hotelOrder.getCouponId();

            if(couponId!=0){
                couponMapper.updateStatues(0,hotelOrder.getUserId(),couponId);
            }


            if(!(resultRoll ==days)){

                throw  new BusinessException("回滚库存失败");
            }

        }

    }
}
