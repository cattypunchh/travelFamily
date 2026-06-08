package com.travelfamilies.mapper;

import com.travelfamilies.pojo.MqMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MqMessageMapper {

    @Insert("INSERT INTO mq_message (id, order_id, exchange, routing_key, body, status, retry_count, max_retry, create_time, update_time) " +
            "VALUES (#{id}, #{orderId}, #{exchange}, #{routingKey}, #{body}, #{status}, #{retryCount}, #{maxRetry}, NOW(), NOW())")
    int insert(MqMessage message);

    @Select("SELECT * FROM mq_message WHERE status = 0 ORDER BY create_time ASC LIMIT #{limit}")
    List<MqMessage> findPending(@Param("limit") int limit);

    @Update("UPDATE mq_message SET status = #{status}, retry_count = retry_count + 1, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") int status);
}
