package com.travelfamilies.tools;

import com.travelfamilies.config.RabbitConfig;
import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.mapper.CouponMapper;
import com.travelfamilies.mapper.OrderMapper;
import com.travelfamilies.pojo.HotelOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class OrderCancelConsumer {

    private final OrderMapper orderMapper;
    private final CouponMapper couponMapper;
    private final CalculateDays calculateDays;

    @RabbitListener(queues = RabbitConfig.ORDER_CANCEL_QUEUE)
    @Transactional
    public void updateOrderStatus(String orderId) throws BusinessException {

        long id = Long.parseLong(orderId);
        HotelOrder hotelOrder = orderMapper.getOrder(id);

        if (hotelOrder.getStatus() == 0) {

            orderMapper.updateStatus(id, 3);

            Map<String, Object> calculate = calculateDays.calculate(hotelOrder);
            long couponId = hotelOrder.getCouponId();

            if (couponId != 0) {
                couponMapper.updateStatues(0, hotelOrder.getUserId(), couponId);
            }

            if (!(((int) calculate.get("result")) == ((long) calculate.get("days")))) {

                throw new BusinessException("回滚库存失败");
            }

        }

    }
}
