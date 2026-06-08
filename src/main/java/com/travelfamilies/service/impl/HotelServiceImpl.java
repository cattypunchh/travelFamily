package com.travelfamilies.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.travelfamilies.mapper.CouponMapper;
import com.travelfamilies.mapper.HotelMapper;
import com.travelfamilies.mapper.ImagesMapper;
import com.travelfamilies.pojo.*;
import com.travelfamilies.request.GetDataRequest;
import com.travelfamilies.request.hotelRequest.*;
import com.travelfamilies.request.userRequest.GetHotelByStatusRequest;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addHotel(HotelRequest addHotelRequest) throws Exception {

        String result = hotelMapper.getHotelByName(addHotelRequest.getName(), addHotelRequest.getCity());

        if (StringUtils.hasText(result)) {
            return Result.failed("该城市已有该名称的酒店，请检查填写信息是否有误");
        }

        List<String> images = addHotelRequest.getImages();
        String main_pic = images.get(0);

        long id = cn.hutool.core.util.IdUtil.getSnowflakeNextId();
        int resultHotel = hotelMapper.addHotel(main_pic, addHotelRequest, id);

        if (resultHotel < 1) {

            return Result.failed("上传酒店信息失败");
        }

        int resultAdd = imagesMapper.addImages(id, 1, images);

        if (resultAdd < 1) {

            throw new RuntimeException("酒店图片上传失败，请重试");
        }

        return Result.success(id);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addRoom(RoomRequest addRoomRequest) throws Exception {

        List<String> images = addRoomRequest.getImages();
        String main_pic = images.get(0);
        int resultRoom = hotelMapper.addRoom(main_pic, addRoomRequest);

        if (resultRoom < 1) {

            return Result.failed("该酒店房型信息录入失败");
        }
        Long hotelId = Long.valueOf(addRoomRequest.getHotelId());
        int roomId = addRoomRequest.getId();
        Double price = addRoomRequest.getDefaultPrice();

        stringRedisTemplate.opsForValue().set(RedisConstant.HOTEL_ROOM_PRICE + hotelId + ":" + roomId,
                String.valueOf(price));

        if (imagesMapper.addImages((long) roomId, 2, images) == 0) {

            throw new RuntimeException("上传房型图片失败");
        }
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<String> thirtyDays = new ArrayList<>();

        thirtyDays.add(now.format(formatter));
        for (int i = 1; i < 31; i++) {
            now = now.plusDays(1);
            String day = now.format(formatter);
            thirtyDays.add(day);
        }

        int resultStock = hotelMapper.addStock(roomId, hotelId, addRoomRequest.getTotalInventory(), thirtyDays, price);

        if (resultStock < 0) {

            throw new RuntimeException("该房型已录入，但是同时创建的日库存信息失败");

        }

        return Result.success();

    }

    @Override
    public Result<?> updateHotel(HotelRequest updateHotelRequest, long id) {

        String hotelName = hotelMapper.getHotelByID(id);
        if (!StringUtils.hasText(hotelName)) {

            return Result.failed("该酒店可能被临时删除了，请刷新查看");
        }

        int delResult = imagesMapper.deleteImages(id, 1);
        if (!(delResult > 0)) {
            return Result.failed("删除旧照片失败");
        }

        List<String> images = updateHotelRequest.getImages();

        String main_pic = images.get(0);

        imagesMapper.addImages(id, 1, images);
        return hotelMapper.updateHotel(updateHotelRequest, id, main_pic) > 0 ? Result.success() : Result.failed("更新酒店信息错误");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateRoom(RoomRequest updateRoomRequest){

        String getResult = hotelMapper.getRoomById(updateRoomRequest.getId());
        if (!StringUtils.hasText(getResult)) {

            return Result.failed("该酒店的房型可能被临时删除了，请刷新查看");
        }
        int resultRoom = hotelMapper.updateRoom(updateRoomRequest);

        if (resultRoom < 1) {

            return Result.failed("更新房型信息失败");
        }

        Double newPrice = updateRoomRequest.getDefaultPrice();
        int roomId = updateRoomRequest.getId();
        long hotelId = Long.parseLong(updateRoomRequest.getHotelId());
        String mess=hotelId + ":" + roomId;
        String key = RedisConstant.HOTEL_ROOM_PRICE +mess ;
        stringRedisTemplate.opsForValue().set(RedisConstant.HOTEL_ROOM_STOCK+mess, String.valueOf(updateRoomRequest.getTotalInventory()));
        String oldPrice = stringRedisTemplate.opsForValue().get(key);

        if(oldPrice == null||Double.parseDouble(oldPrice) != newPrice){

            if (hotelMapper.updateStockPrice(newPrice, roomId, hotelId) < 1) {

                throw  new RuntimeException("由于您更新了该房型的价格，系统尝试更新每日价格，但失败，请去专门修改每日价格的地方修改");
            }

            stringRedisTemplate.opsForValue().set(key,String.valueOf(newPrice));
        }
        return Result.success();
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

        if (hotelMapper.updateDayMess(updateDayMessRequest) <1) {
            return Result.failed("更新该天信息失败，请重试");

        }
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
    }


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
            } else {
                return Result.failed("该房间库存信息丢失");
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

        rooms.forEach(room -> {
            int roomId = room.getId();
            room.setTotalInventory(collect.get(roomId).getStock());
            room.setDefaultPrice(collect.get(roomId).getPrice());
        });

        List<Long> id = new ArrayList<>();
        id.add(hotelId);
        List<Image> images = imagesMapper.getImages(id, 1);
        List<Coupon> coupons = couponMapper.getCouponByHotelId(hotelId);
        List<Coupon> couponGeneral = couponMapper.getCouponGeneral();
        coupons.addAll(couponGeneral);

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

        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusDays(30);

        if (start.isBefore(today)) {
            return Result.failed("入住日期不能早于今天");
        }
        if (end.isAfter(maxDate.plusDays(1))) {
            return Result.failed("仅支持预订今日起30天内的房间");
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

    @Override
    public Result<?> getHotelOwner(GetDataRequest getDataRequest, long adminId) {


        List<Long> hotelIds = hotelMapper.getHotelByAdmin(adminId);

        if (hotelIds == null || hotelIds.isEmpty()) {

            return Result.failed("您还没有申请酒店入驻");
        }

        List<Hotel> hotels = new ArrayList<>();
        for (Long hotelId : hotelIds) {

            Hotel hotel = hotelMapper.getHotel(hotelId);
            hotels.add(hotel);
        }

        return Result.success(hotels);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteHotel(long hotelId) {

        /*删除酒店照片 库存照片 库存 房型*/
        imagesMapper.deleteImages(hotelId, 1);
        List<Room> rooms = hotelMapper.getRoomByHotel(hotelId);

        List<Integer> roomIds = new ArrayList<>();
        for (Room room : rooms) {
            int roomId = room.getId();
            roomIds.add(roomId);
            imagesMapper.deleteImages(roomId, 2);
        }

        hotelMapper.deleteRoomType(roomIds);

        hotelMapper.deleteRoomStock(hotelId);

        int result = hotelMapper.deleteHotel(hotelId);

        return result > 0 ? Result.success()
                : Result.failed("删除酒店失败");
    }

    @Override
    public Result<?> getRoom(long hotelId) {

        List<Room> roomByHotel = hotelMapper.getRoomByHotel(hotelId);
        if (roomByHotel == null || roomByHotel.isEmpty()) {

            return Result.failed("该酒店还没有房型");
        }
        return Result.success(roomByHotel);
    }

    @Override
    public Result<?> getStockByDay(long hotelId, int roomId, String date) {

        GetRoomDayMess dayMessByDay = hotelMapper.getDayMessByDay(roomId, date);

        if (dayMessByDay == null) {

            return Result.failed("该天信息加载不出来，请重试");
        }
        return Result.success(dayMessByDay);
    }

    @Override
    public Result<?> deleteRoom(int roomId) {

        //删除库存加照片就行
        imagesMapper.deleteImages(roomId, 2);

        hotelMapper.deleteRoomStockByRoom(roomId);

        List<Integer> roomIds = new ArrayList<>();
        roomIds.add(roomId);

        hotelMapper.deleteRoomType(roomIds);

        return Result.success();
    }

    @Override
    public Result<?> approvalHotel(long id, int status) {

        int result = hotelMapper.updateStatus(id, status);
        return result > 0 ? Result.success() : Result.failed("审核失败");
    }

    @Override
    public Result<?> getHotelByStatus(GetHotelByStatusRequest getHotelByStatusRequest) {

        PageHelper.startPage(getHotelByStatusRequest.requestPage(), getHotelByStatusRequest.requestNum());

        List<Hotel> hotels = hotelMapper.getHotelByStatus(getHotelByStatusRequest.status());

        PageInfo<Hotel> pageInfo = new PageInfo<>(hotels);
        return Result.success(pageInfo);
    }

    @Override
    public Result<?> getAllHotels(GetDataRequest getDataRequest) {

        PageHelper.startPage(getDataRequest.requestPage(), getDataRequest.requestNum());

        List<Hotel> hotels = hotelMapper.getAllHotels();

        PageInfo<Hotel> pageInfo = new PageInfo<>(hotels);
        return Result.success(pageInfo);
    }

    @Override
    public Result<?> getHotelWOrder(long hotelId) {

        GetHotelResponse getHotelResponse = new GetHotelResponse();
        BeanUtils.copyProperties(hotelMapper.getHotel(hotelId), getHotelResponse);

        return Result.success(getHotelResponse);
    }

}
