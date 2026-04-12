package com.travelfamilies.task;

import com.travelfamilies.mapper.SpotMapper;
import com.travelfamilies.tools.RedisConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Objects;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class SpotTask {

    private final StringRedisTemplate stringRedisTemplate;
    private final SpotMapper spotMapper;

    @Scheduled(cron = "0 0 * * * *")
    public void dynamicViews() {

        Set<String> keys = stringRedisTemplate.keys(RedisConstant.SPOT_VIEWS + "*");

        for (String key : keys) {

            int spotId = Integer.parseInt(key.substring(RedisConstant.SPOT_VIEWS.length()));
            int views = Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(key)));

            spotMapper.updateViews(spotId, views);
        }
        /* 优化 ：可批量更新或者在redis中创一个存所有景点id的set*/
    }
}
