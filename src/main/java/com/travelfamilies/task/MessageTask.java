package com.travelfamilies.task;

import com.travelfamilies.mapper.MqMessageMapper;
import com.travelfamilies.pojo.MqMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时扫描本地消息表 mq_message，将待发送的消息投递到 RabbitMQ
 * 实现消息与业务操作的最终一致性
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageTask {

    private final MqMessageMapper mqMessageMapper;
    private final RabbitTemplate rabbitTemplate;

    /**
     * 每 10 秒扫描一次待发送消息
     */
    @Scheduled(fixedDelay = 10000)
    public void sendPendingMessages() {
        List<MqMessage> messages = mqMessageMapper.findPending(100);
        for (MqMessage message : messages) {
            try {
                rabbitTemplate.convertAndSend(message.getExchange(), message.getRoutingKey(), message.getBody());
                mqMessageMapper.updateStatus(message.getId(), 1);
                log.info("消息投递成功: id={}, orderId={}", message.getId(), message.getOrderId());
            } catch (Exception e) {
                log.error("消息投递失败: id={}, orderId={}, retryCount={}", message.getId(), message.getOrderId(), message.getRetryCount(), e);
                // 超过最大重试次数标记为失败，否则递增重试次数（status 保持 0）
                if (message.getRetryCount() + 1 >= message.getMaxRetry()) {
                    mqMessageMapper.updateStatus(message.getId(), 2);
                    log.error("消息投递达到最大重试次数，标记为失败: id={}", message.getId());
                } else {
                    mqMessageMapper.updateStatus(message.getId(), 0);
                }
            }
        }
    }
}
