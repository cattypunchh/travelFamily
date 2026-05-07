package com.travelfamilies.service.impl;

import com.travelfamilies.mapper.ImagesMapper;
import com.travelfamilies.mapper.SpotMapper;
import com.travelfamilies.pojo.Image;
import com.travelfamilies.pojo.Spot;
import com.travelfamilies.pojo.SpotVO;
import com.travelfamilies.request.spotRequest.QuerySpotRequest;
import com.travelfamilies.request.spotRequest.SpotRequest;
import com.travelfamilies.request.spotRequest.UpdateDetailRequest;
import com.travelfamilies.response.Result;
import com.travelfamilies.response.SpotResponse;
import com.travelfamilies.service.SpotService;
import com.travelfamilies.tools.RedisConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SpotServiceImpl implements SpotService {

    private final SpotMapper spotMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ImagesMapper imagesMapper;

    @Override
    @Cacheable(value = RedisConstant.SPOT_TOP10_LIST, key = "'top10'", sync = true)
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
    public Result<?> getSpotDetail(Long id, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");

        /*防止某用户恶意刷浏览量*/
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(RedisConstant.SPOT_VIEWS_USER + userId + ":" + id,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "起该用户在一个小时内已访问过该景点信息，文章id为：" + id,
                RedisConstant.SPOT_VIEWS_USER_EXPIRES, TimeUnit.MILLISECONDS);

        if (Boolean.TRUE.equals(result)) {

            stringRedisTemplate.opsForValue().increment(RedisConstant.SPOT_VIEWS + id);
        }
        List<Long> spotId = new ArrayList<>();
        spotId.add(id);
        Spot spot = spotMapper.getSpotDetail(id);
        SpotVO spotVO = new SpotVO();
        BeanUtils.copyProperties(spot, spotVO);
        List<Image> images = imagesMapper.getImages(spotId, 3);
        spot.setViews(Integer.valueOf(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(RedisConstant.SPOT_VIEWS + id))));
        spotVO.setImageUrls(images);
        return Result.success(spotVO);
    }

    @Override
    @CacheEvict(value = RedisConstant.SPOT_TOP10_LIST, key = "'top10'")
    public Result<?> addSpot(SpotRequest spotRequest) {


        if (spotMapper.getSpotId(spotRequest.getName()) !=null)
            return Result.failed("该景点已添加过，或者请检查景点名字是否重复");
        Spot spot = new Spot();
        BeanUtils.copyProperties(spotRequest, spot);

        List<String> images = spotRequest.getImageUrls();
        spot.setImageUrls(images.get(0));
        long id = cn.hutool.core.util.IdUtil.getSnowflakeNextId();
        spot.setId(id);
        int result = spotMapper.addSpot(spot);

        imagesMapper.addImages(spot.getId(), 3, spotRequest.getImageUrls());

        if (result > 0) {

            stringRedisTemplate.opsForSet().add(RedisConstant.SPOT_TYPE_DETAIL, spot.getType());
            stringRedisTemplate.opsForValue().set(RedisConstant.COMMENT_SPOT_SCORE + spot.getId(), "0.0");
            return Result.success();
        }

        return Result.failed("添加失败，请重新尝试");

    }

    @Override
    @CacheEvict(value = RedisConstant.SPOT_TOP10_LIST, key = "'top10'")
    public Result<?> updateSpot(UpdateDetailRequest updateDetailRequest,long id) {

        return spotMapper.updateSpot(updateDetailRequest,id) == 1 ?
                Result.success() :
                Result.failed("更新失败");

    }

    @Override
    @CacheEvict(value = RedisConstant.SPOT_TOP10_LIST, key = "'top10'")
    public Result<?> deleteSpot(Long id) {

        return spotMapper.deleteSpot(id) == 1 ?
                Result.success() :
                Result.failed("删除失败");

    }


}
