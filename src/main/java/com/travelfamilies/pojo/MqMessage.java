package com.travelfamilies.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MqMessage {

    private Long id;
    private Long orderId;
    private String exchange;
    private String routingKey;
    private String body;
    /** 0-待发送 1-已发送 2-已失败 */
    private Integer status;
    private Integer retryCount;
    private Integer maxRetry;
    private String createTime;
    private String updateTime;
}
