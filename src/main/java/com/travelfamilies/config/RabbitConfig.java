package com.travelfamilies.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    /*死信队列和交换机*/
    public static final String ORDER_TTL_EXCHANGE = "order.ttl.exchange";
    public static final String ORDER_TTL_QUEUE = "order.ttl.queue";

    /*正常使用的*/
    public static final String ORDER_CANCEL_EXCHANGE = "order.cancel.exchange";
    public static final String ORDER_CANCEL_QUEUE = "order.cancel.queue";

    @Bean
    public Queue orderTtlQueue() {

        return QueueBuilder.durable(ORDER_TTL_QUEUE)
                .deadLetterExchange(ORDER_CANCEL_EXCHANGE)
                .deadLetterRoutingKey("cancel")
                .ttl(60*1000)
                .build();
    }

    @Bean
    public Exchange orderTtlExchange() {

        return new DirectExchange(ORDER_TTL_EXCHANGE);
    }

    @Bean
    public Binding orderTtlBinding() {

        return BindingBuilder.bind(orderTtlQueue()).to(orderTtlExchange()).with("ttl").noargs();
    }
    @Bean
    public Queue orderCancelQueue() {

        return QueueBuilder.durable(ORDER_CANCEL_QUEUE).build();
    }
    @Bean
    public Exchange orderCancelExchange() {
        return new DirectExchange(ORDER_CANCEL_EXCHANGE);
    }
    @Bean
    public Binding orderCancelBinding() {

        return BindingBuilder.bind(orderCancelQueue()).to(orderCancelExchange()).with("cancel").noargs();
    }
}
