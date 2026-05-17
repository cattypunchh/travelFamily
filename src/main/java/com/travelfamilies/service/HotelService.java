package com.travelfamilies.service;

import com.travelfamilies.request.GetDataRequest;
import com.travelfamilies.request.hotelRequest.*;
import com.travelfamilies.request.userRequest.GetHotelByStatusRequest;
import com.travelfamilies.response.Result;

public interface HotelService {
    Result<?> addHotel(HotelRequest addHotelRequest) throws Exception;

    Result<?> addRoom(RoomRequest addRoomRequest) throws Exception;

    Result<?> updateHotel(HotelRequest updateHotelRequest, long id);

    Result<?> updateRoom(RoomRequest updateRoomRequest) throws Exception;

    Result<?> updateDayMess(UpdateDayMessRequest updateDayMessRequest);

    Result<?> get(GetHotelRequest getHotelRequest);

    Result<?> getDetail(GetRoomRequest getRoomRequest);

    Result<?> searchHotel(QueryHotelRequest queryHotelRequest);

    Result<?> reserveRoom(ReserveRoomRequest reserveRoomRequest);

    Result<?> locateRoom(LocateRoomRequest locateRoomRequest, long userId);

    Result<?> getHotelOwner(GetDataRequest getDataRequest, long adminId);

    Result<?> deleteHotel(long hotelId);

    Result<?> getRoom(long hotelId);

    Result<?> getStockByDay(long hotelId, int roomId, String date);

    Result<?> deleteRoom(int roomId);

    Result<?> approvalHotel(long id, int status);

    Result<?> getHotelByStatus(GetHotelByStatusRequest getHotelByStatusRequest);

    Result<?> getAllHotels(GetDataRequest getDataRequest);

    Result<?> getHotelWOrder(long hotelId);

    // Result<?> getRoomStock(long l, Integer integer);
}
