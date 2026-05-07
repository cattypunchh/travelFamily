package com.travelfamilies.controller;


import com.travelfamilies.request.hotelRequest.*;
import com.travelfamilies.response.Result;
import com.travelfamilies.service.HotelService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotel")
@RequiredArgsConstructor
public class HotelController {


    private final HotelService hotelService;

    @PostMapping
    public Result<?> addHotel(@RequestBody HotelRequest addHotelRequest) {

        return hotelService.addHotel(addHotelRequest);
    }

    @PostMapping("/room")
    public Result<?> addRoom(@RequestBody RoomRequest roomRequest) {

        return hotelService.addRoom(roomRequest);
    }

    @PutMapping("/{id}")
    public Result<?> modify(@PathVariable long id,@RequestBody HotelRequest updateHotelRequest) {

        return hotelService.updateHotel(updateHotelRequest,id);
    }

    @PutMapping("/room")
    public Result<?> updateRoom(@RequestBody RoomRequest roomRequest) {

        return hotelService.updateRoom(roomRequest);
    }

    @PutMapping("/dayMess")
    public Result<?> updateDayMess(@RequestBody UpdateDayMessRequest  updateDayMessRequest) {

        return hotelService.updateDayMess(updateDayMessRequest);
    }

    @GetMapping
    public Result<?> get(@RequestBody GetHotelRequest getHotelRequest) {

        return hotelService.get(getHotelRequest);
    }

    @GetMapping("/room")
    public Result<?> getDetail(@RequestBody GetRoomRequest getRoomRequest) {

        return hotelService.getDetail(getRoomRequest);
    }

    @GetMapping("/search")
    public Result<?> searchHotel(@RequestBody QueryHotelRequest  queryHotelRequest) {

        return hotelService.searchHotel(queryHotelRequest);
    }

    @GetMapping("/reserve")
    public Result<?> reserveRoom(@RequestBody ReserveRoomRequest reserveRoomRequest) {

        return hotelService.reserveRoom(reserveRoomRequest);
    }

    @GetMapping("/locate")
    public Result<?> locateHotel(@RequestBody LocateRoomRequest locateRoomRequest, HttpServletRequest httpServletRequest) {

        long userId= (long) httpServletRequest.getAttribute("userID");
        return hotelService.locateRoom(locateRoomRequest,userId);
    }


}
