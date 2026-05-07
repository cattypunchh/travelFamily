package com.travelfamilies.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.travelfamilies.mapper.CouponMapper;
import com.travelfamilies.mapper.HotelMapper;
import com.travelfamilies.mapper.ImagesMapper;
import com.travelfamilies.pojo.*;
import com.travelfamilies.request.hotelRequest.*;
import com.travelfamilies.response.GetHotelResponse;
import com.travelfamilies.response.GetRoomDayMess;
import com.travelfamilies.response.Result;
import com.travelfamilies.service.HotelService;
import com.travelfamilies.tools.RedisConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelMapper hotelMapper;
    private final ImagesMapper imagesMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final CouponMapper couponMapper;

    @Override
    public Result<?> addHotel(HotelRequest addHotelRequest) {

        String result = hotelMapper.getHotelByName(addHotelRequest.getName(), addHotelRequest.getCity());

        if (StringUtils.hasText(result)) {
            return Result.failed("该城市已有该名称的酒店，请检查填写信息是否有误");
        }

        List<String> images = addHotelRequest.getImages();
        String main_pic = images.get(0);

        long id = cn.hutool.core.util.IdUtil.getSnowflakeNextId();
        int resultHotel = hotelMapper.addHotel(main_pic, addHotelRequest,id);

        if (resultHotel > 0) {
            return imagesMapper.addImages(id, 1, images) > 0
                    ? Result.success(id)
                    : Result.failed("图片上传失败，请重试");
        }

        return Result.failed("上传酒店信息失败");
    }

    @Override
    @Transactional
    public Result<?> addRoom(RoomRequest addRoomRequest) {

        List<String> images = addRoomRequest.getImages();
        String main_pic = images.get(0);
        System.out.println(images.get(0));
        int resultRoom = hotelMapper.addRoom(main_pic, addRoomRequest);
        if (resultRoom > 0) {

            LocalDate now = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            List<String> thirtyDays = new ArrayList<>();

            thirtyDays.add(now.format(formatter));
            for (int i = 1; i < 31; i++) {
                now = now.plusDays(1);
                String day = now.format(formatter);
                thirtyDays.add(day);
            }

            int roomId = addRoomRequest.getId();
            Long hotelId = addRoomRequest.getHotelId();
            Double price = addRoomRequest.getDefaultPrice();
            if (imagesMapper.addImages((long)roomId, 2, images) == 0) {

                return Result.failed("上传房型图片失败");
            }
            int resultStock = hotelMapper.addStock(roomId, hotelId, addRoomRequest.getTotalInventory(), thirtyDays, price);

            if (resultStock > 0) {

                stringRedisTemplate.opsForValue().set(RedisConstant.HOTEL_ROOM_PRICE + hotelId + ":" + roomId,
                        String.valueOf(price));
                return Result.success();
            }
            return Result.failed("该房型已录入，但是同时创建的日库存信息失败");


        }
        return Result.failed("该酒店房型信息录入失败");
    }

    @Override
    public Result<?> updateHotel(HotelRequest updateHotelRequest,long id) {

        String hotelName = hotelMapper.getHotelByID(id);
        if (!StringUtils.hasText(hotelName)) {

            return Result.failed("该酒店可能被临时删除了，请刷新查看");
        }
        return hotelMapper.updateHotel(updateHotelRequest,id) > 0 ? Result.success() : Result.failed("更新酒店信息错误");
    }

    @Override
    public Result<?> updateRoom(RoomRequest updateRoomRequest) {

        String getResult = hotelMapper.getRoomById(updateRoomRequest.getId());
        if (!StringUtils.hasText(getResult)) {

            return Result.failed("该酒店的房型可能被临时删除了，请刷新查看");
        }
        int resultRoom = hotelMapper.updateRoom(updateRoomRequest);

        if (resultRoom > 0) {

            Double newPrice = updateRoomRequest.getDefaultPrice();
            int roomId = updateRoomRequest.getId();
            long hotelId = updateRoomRequest.getHotelId();
            String key = RedisConstant.HOTEL_ROOM_PRICE + hotelId + ":" + roomId;
            stringRedisTemplate.opsForValue().set(key, String.valueOf(updateRoomRequest.getTotalInventory()));
            String oldPrice = stringRedisTemplate.opsForValue().get(key);
            if ((!StringUtils.hasText(oldPrice)) || (Double.parseDouble(oldPrice) != newPrice)) {

                if (hotelMapper.updateStockPrice(newPrice, roomId, hotelId) > 0) {
                    stringRedisTemplate.delete(key);
                    return Result.success();
                }

                return Result.failed("由于您更新了该房型的价格，系统尝试更新每日价格，但失败，请去专门修改每日价格的地方修改");
            }

        }

        return Result.failed("更新房型信息失败");
    }

    @Override
    public Result<?> updateDayMess(UpdateDayMessRequest updateDayMessRequest) {

        if (!(updateDayMessRequest.price() >= 0 && updateDayMessRequest.stock() >= 0)) {
            return Result.failed("请输入正确的修改信息");
        }

        String time = updateDayMessRequest.time();
        int roomTypeId = updateDayMessRequest.roomTypeId();
        Integer resultStock = hotelMapper.getStockByDay(time, roomTypeId);
        if (resultStock == null) {
            return Result.failed("该天的信息已不存在，请刷新重试，只能修改未来三十天的信息");
        }

        if (hotelMapper.updateDayMess(updateDayMessRequest) > 0) {

            long hotelId = updateDayMessRequest.hotelId();
            String key = RedisConstant.HOTEL_ROOM_DYNAMIC + hotelId + ":" + time;
            String data = (String) stringRedisTemplate.opsForHash().get(key, String.valueOf(roomTypeId));
            if (StringUtils.hasText(data)) {

                JSONObject roomMess = JSON.parseObject(data);
                roomMess.put("price", updateDayMessRequest.price());
                roomMess.put("stock", updateDayMessRequest.stock());
                stringRedisTemplate.opsForHash().put(key, String.valueOf(roomTypeId), JSON.toJSONString(roomMess));

            }
            return Result.success();
        } else {

            return Result.failed("更新该天信息失败，请重试");
        }


    }

    //
    @Override
    public Result<?> get(GetHotelRequest getHotelRequest) {

        PageHelper.startPage(getHotelRequest.getPageNum(), getHotelRequest.getPageSize());
        List<GetHotelResponse> hotels = hotelMapper.get();
        PageInfo<GetHotelResponse> pageInfo = new PageInfo<>(hotels);
        return Result.success(pageInfo);
    }

    @Override
    public Result<?> getDetail(GetRoomRequest getRoomRequest) {

        long hotelId = getRoomRequest.hotelId();
        Hotel hotel = hotelMapper.getHotel(hotelId);

        if (hotel == null) {

            return Result.failed("该酒店已不存在");
        }
        HotelVO newHotel = new HotelVO();
        BeanUtils.copyProperties(hotel, newHotel);
        LocalDate end = LocalDate.parse(getRoomRequest.endTime());
        String localDay = end.minusDays(1).toString();
        List<Room> rooms = hotelMapper.getRooms(getRoomRequest, localDay);

        if (rooms == null) {
            newHotel.setRooms(null);
            newHotel.setImages(null);
            return Result.success(newHotel);
        }

        List<Integer> roomsIds = rooms.stream().map(Room::getId).toList();
        String startTime = getRoomRequest.startTime();

        String dayKey = RedisConstant.HOTEL_ROOM_DYNAMIC + hotelId + ":" + startTime;
        Map<Object, Object> roomMess = stringRedisTemplate.opsForHash().entries(dayKey);
        List<GetRoomDayMess> dayMess;
        if (roomMess.isEmpty()) {
            dayMess = hotelMapper.getDayMess(roomsIds, startTime);
            if (dayMess != null) {
                Map<String, String> map = dayMess.stream()
                        .collect(Collectors.toMap(
                                item -> String.valueOf(item.getRoomTypeId()),
                                item -> JSON.toJSON(item).toString())
                        );

                stringRedisTemplate.opsForHash().putAll(dayKey, map);
                stringRedisTemplate.expire(dayKey, RedisConstant.HOTEL_ROOM_DYNAMIC_EXPIRES, TimeUnit.DAYS);
            }

        } else {

            dayMess = roomMess.values().stream()
                    .map(json -> JSON.parseObject((String) json, GetRoomDayMess.class))
                    .collect(Collectors.toList());
        }

        Map<Integer, GetRoomDayMess> collect = dayMess.stream()
                .collect(Collectors.toMap(
                        GetRoomDayMess::getRoomTypeId,
                        item -> item)
                );

        for (Room room : rooms) {

            int roomId = room.getId();
            room.setTotalInventory(collect.get(roomId).getStock());
            room.setDefaultPrice(collect.get(roomId).getPrice());
        }
        List<Long> id=new ArrayList<>();
        List<Image> images = imagesMapper.getImages(id, 2);
        List<Coupon> coupons = couponMapper.getCouponByHotelId(hotelId);

        newHotel.setRooms(rooms);
        newHotel.setImages(images);
        newHotel.setCoupons(coupons);
        return Result.success(newHotel);
    }

    @Override
    public Result<?> searchHotel(QueryHotelRequest queryHotelRequest) {

        List<GetHotelResponse> hotels = hotelMapper.searchHotel(queryHotelRequest);
        return hotels == null ? Result.success("没有这样的酒店") :
                Result.success(hotels);

    }

    @Override
    public Result<?> reserveRoom(ReserveRoomRequest reserveRoomRequest) {

        LocalDate start = LocalDate.parse(reserveRoomRequest.startTime());
        LocalDate end = LocalDate.parse(reserveRoomRequest.endTime());

        if (!end.isAfter(start)) {

            return Result.failed("退房日期必须在住房日期之后");
        }

        String endDay = end.minusDays(1).toString();

        List<GetHotelResponse> hotels = hotelMapper.searchHotelByCityAndTime(reserveRoomRequest, endDay);

        return Result.success(hotels);
    }

    @Override
    public Result<?> locateRoom(LocateRoomRequest locateRoomRequest, long userId) {

        LocalDate endTime = LocalDate.parse(locateRoomRequest.endTime());
        String locateDays = String.valueOf(endTime.minusDays(1));

        return hotelMapper.getLocateRoom(locateRoomRequest, locateDays) > 0 ?
                Result.failed("库存告急，这个时间段暂时无房")
                : Result.success();

    }
}
