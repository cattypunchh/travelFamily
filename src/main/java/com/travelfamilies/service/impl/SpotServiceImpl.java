package com.travelfamilies.service.impl;

import com.travelfamilies.mapper.SpotMapper;
import com.travelfamilies.pojo.Spot;
import com.travelfamilies.request.spotRequest.QuerySpotRequest;
import com.travelfamilies.request.spotRequest.UpdateDetailRequest;
import com.travelfamilies.response.Result;
import com.travelfamilies.response.SpotResponse;
import com.travelfamilies.service.SpotService;
import com.travelfamilies.tools.RedisConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SpotServiceImpl implements SpotService {

    private final SpotMapper spotMapper;
    private final StringRedisTemplate stringRedisTemplate;


    @Override
    @Cacheable(value = RedisConstant.SPOT_TOP10_LIST, key = "'top10'",sync = true)
    public Result<?> getHotSpot() {

        List<SpotResponse> spotResponse = spotMapper.getHotSpot();
        return Result.success(spotResponse);
    }

    @Override
    public Result<?> getSpot(QuerySpotRequest querySpotRequest) {

        //前端搜索框旁边使用
        List<String> typeList = List.of(new String[]{"name", "city", "type"});


        List<SpotResponse> spotResponse = spotMapper.getSpot(querySpotRequest);

        if (spotResponse == null) {

            return Result.failed("无该景点");
        }

        return Result.success(spotResponse);
    }

    @Override
    public Result<?> getSpotDetail(int id, HttpServletRequest httpServletRequest) {

        int userId = (int) httpServletRequest.getAttribute("userID");

        /*防止某用户恶意刷浏览量*/
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(RedisConstant.SPOT_VIEWS_USER + userId+":"+id,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+ "起该用户在一个小时内已访问过该景点信息，文章id为：" + id,
                RedisConstant.SPOT_VIEWS_USER_EXPIRES, TimeUnit.MILLISECONDS);

        if (Boolean.TRUE.equals(result)) {

            stringRedisTemplate.opsForValue().increment(RedisConstant.SPOT_VIEWS + id);
        }

        //TODO  定时回写数据库覆盖原本的浏览量
        Spot spot = spotMapper.getSpotDetail(id);
        spot.setViews(Integer.valueOf(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(RedisConstant.SPOT_VIEWS + id))));
        return Result.success(spot);
    }

    @Override
    @CacheEvict(value = RedisConstant.SPOT_TOP10_LIST, key = "'top10'")
    public Result<?> addSpot(Spot spot) {

        int result = spotMapper.addSpot(spot);

        if (result > 0) {

            stringRedisTemplate.opsForSet().add(RedisConstant.SPOT_TYPE_DETAIL, spot.getType());
            return Result.success();
        }

        return Result.failed("添加失败，请重新尝试");

    }

    @Override
    @CacheEvict(value = RedisConstant.SPOT_TOP10_LIST, key = "'top10'")
    public Result<?> updateSpot(UpdateDetailRequest updateDetailRequest) {

        return spotMapper.updateSpot(updateDetailRequest) == 1 ?
                Result.success() :
                Result.failed("更新失败");

    }

    @Override
    @CacheEvict(value = RedisConstant.SPOT_TOP10_LIST, key = "'top10'")
    public Result<?> deleteSpot(int id) {

        return spotMapper.deleteSpot(id)==1?
                Result.success():
                Result.failed("删除失败");

    }


}
