package com.travelfamilies.task;

import com.travelfamilies.mapper.SpotMapper;
import com.travelfamilies.tools.RedisConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SpotTask {

    private final StringRedisTemplate stringRedisTemplate;
    private final SpotMapper spotMapper;

    @Scheduled(cron = "0 */5 * * * *")
    public void dynamicViews() {
        log.info("定时任务开始了");
        Set<String> keys = stringRedisTemplate.keys(RedisConstant.SPOT_VIEWS + "*");

        for (String key : keys) {

            long spotId = Long.parseLong(key.substring(RedisConstant.SPOT_VIEWS.length()));
            int views = Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(key)));

            spotMapper.updateViews(spotId, views);
            log.info("景点浏览量数据库更新成功");
        }
        /* 优化 ：可批量更新或者在redis中创一个存所有景点id的set*/

    }

    //    5s执行一次 0/5 * * * * ?
    /*定时更新酒店和景点的评论数及平均评分*/
    @Scheduled(cron = "0 0 * * * *")
    public void dynamicCountAndStarRating() {

        updateCountByType(RedisConstant.COMMENT_SPOT_COUNT, "spot", "count");
        updateCountByType(RedisConstant.COMMENT_SPOT_SCORE, "spot", "score");

        updateCountByType(RedisConstant.COMMENT_HOTEL_COUNT, "hotel", "count");
        updateCountByType(RedisConstant.COMMENT_HOTEL_SCORER, "hotel", "score");

    }

    private void updateCountByType(String key, String type, String mode) {
        log.info("定时任务开始了");
        Set<String> keys = stringRedisTemplate.keys(key + "*");
        int length = key.length();

        List<Map<String, Object>> updateList = new ArrayList<>();

        long id;
        for (String k : keys) {
            Map<String, Object> map = new HashMap<>();
            id = Long.parseLong(k.substring(length));
            map.put("id", id);

            String warn = "Redis key {} 的值为 null，跳过" + k;
            String result = stringRedisTemplate.opsForValue().get(k);

            if (result == null) {
                log.warn(warn);
                continue;
            }
            double s = Double.parseDouble(result);
            if (mode.equals("count")) {
                map.put("mode", s);
            } else {

                String value = type.equals("spot") ? stringRedisTemplate.opsForValue().get(RedisConstant.COMMENT_SPOT_COUNT + id)
                        : stringRedisTemplate.opsForValue().get(RedisConstant.COMMENT_HOTEL_COUNT + id);

                if (value == null) {
                    log.warn(warn);
                    continue;
                }
                int count = Integer.parseInt(value);
                Double average = count == 0 ? 0.0 : s / count;
                map.put("mode", average);
            }
            updateList.add(map);
        }


        if (updateList.isEmpty()) {
            log.info("updateList 为空，key: {}", key);
            return;
        }
        int result = 0;
        if (type.equals("spot")) {
            if (mode.equals("count")) {
                result = spotMapper.updateCount(updateList);
            } else {

                result = spotMapper.updateStarRating(updateList);
            }

        } else {
            if (mode.equals("count")) {

            } else {

            }
        }

        if (result != 0)
            log.info("数据库同步更新成功");
        log.info("定时任务结束了");
    }


}
