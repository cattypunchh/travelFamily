package com.travelfamilies.service;

import com.travelfamilies.request.hotelRequest.*;
import com.travelfamilies.response.Result;
import jakarta.servlet.http.HttpServletRequest;

public interface HotelService {
    Result<?> addHotel(HotelRequest addHotelRequest);

    Result<?> addRoom(RoomRequest addRoomRequest);

    Result<?> updateHotel(HotelRequest updateHotelRequest,long id);

    Result<?> updateRoom(RoomRequest updateRoomRequest);

    Result<?> updateDayMess(UpdateDayMessRequest updateDayMessRequest);

    Result<?> get(GetHotelRequest getHotelRequest);

    Result<?> getDetail(GetRoomRequest getRoomRequest);

    Result<?> searchHotel(QueryHotelRequest queryHotelRequest);

    Result<?> reserveRoom(ReserveRoomRequest reserveRoomRequest);

    Result<?> locateRoom(LocateRoomRequest locateRoomRequest,long userId);
}
