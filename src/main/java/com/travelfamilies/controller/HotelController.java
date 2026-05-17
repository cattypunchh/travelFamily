package com.travelfamilies.controller;


import com.travelfamilies.request.GetDataRequest;
import com.travelfamilies.request.hotelRequest.*;
import com.travelfamilies.request.userRequest.GetHotelByStatusRequest;
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

    /** 
     * 添加酒店 
     * 
     * @param addHotelRequest 酒店信息请求对象 
     * @param httpServletRequest HTTP 请求对象 
     * @return 操作结果 
     */ 
    @PostMapping
    public Result<?> addHotel(@RequestBody HotelRequest addHotelRequest, HttpServletRequest httpServletRequest) throws Exception {

        long adminId = (long) httpServletRequest.getAttribute("userID");
        addHotelRequest.setAdminId(adminId);
        return hotelService.addHotel(addHotelRequest);
    }

    /** 
     * 添加房型 
     * 
     * @param roomRequest 房型信息请求对象 
     * @return 操作结果 
     */ 
    @PostMapping("/room")
    public Result<?> addRoom(@RequestBody RoomRequest roomRequest) throws Exception {

        return hotelService.addRoom(roomRequest);
    }

    /** 
     * 修改酒店信息 
     * 
     * @param id 酒店主键 ID 
     * @param updateHotelRequest 更新酒店信息请求对象 
     * @return 操作结果 
     */ 
    @PutMapping("/{id}")
    public Result<?> modify(@PathVariable String id, @RequestBody HotelRequest updateHotelRequest) {

        return hotelService.updateHotel(updateHotelRequest, Long.parseLong(id));
    }

    /** 
     * 更新房型信息 
     * 
     * @param roomRequest 房型信息请求对象 
     * @return 操作结果 
     */ 
    @PutMapping("/room")
    public Result<?> updateRoom(@RequestBody RoomRequest roomRequest) throws Exception {

        return hotelService.updateRoom(roomRequest);
    }

    /** 
     * 更新每日信息 
     * 
     * @param updateDayMessRequest 更新每日信息请求对象 
     * @return 操作结果 
     */ 
    @PutMapping("/dayMess")
    public Result<?> updateDayMess(@RequestBody UpdateDayMessRequest updateDayMessRequest) {

        return hotelService.updateDayMess(updateDayMessRequest);
    }

    /** 
     * 查询酒店信息 
     * 
     * @param getHotelRequest 查询酒店请求对象 
     * @return 酒店信息 
     */ 
    @GetMapping
    public Result<?> get(@RequestBody GetHotelRequest getHotelRequest) {

        return hotelService.get(getHotelRequest);
    }

    /** 
     * 获取当前管理员拥有的酒店列表 
     * 
     * @param getDataRequest 分页查询请求对象 
     * @param httpServletRequest HTTP 请求对象 
     * @return 酒店列表 
     */ 
    @PostMapping("/getOwner")
    public Result<?> getOwner(@RequestBody GetDataRequest getDataRequest, HttpServletRequest httpServletRequest) {

        long adminId = (long) httpServletRequest.getAttribute("userID");
        return hotelService.getHotelOwner(getDataRequest, adminId);
    }

    /** 
     * 根据酒店 ID 查询有房房型详情
     * 
     * @param getRoomRequest 查询房型请求对象 
     * @return 房型详细信息 
     */ 
    @PostMapping("/getRoom")
    public Result<?> getDetail(@RequestBody GetRoomRequest getRoomRequest) {

        return hotelService.getDetail(getRoomRequest);
    }

    /** 
     * 搜索酒店 
     * 
     * @param queryHotelRequest 搜索酒店请求对象 
     * @return 搜索结果 
     */ 
    @GetMapping("/search")
    public Result<?> searchHotel(@RequestBody QueryHotelRequest queryHotelRequest) {

        return hotelService.searchHotel(queryHotelRequest);
    }

    /** 
     * 根据行程查询酒店
     * 
     * @param reserveRoomRequest 查询酒店请求对象
     * @return 操作结果 
     */ 
    @PostMapping("/reserve")
    public Result<?> reserveRoom(@RequestBody ReserveRoomRequest reserveRoomRequest) {

        return hotelService.reserveRoom(reserveRoomRequest);
    }

    /** 
     * 预订房间
     * 
     * @param locateRoomRequest 预订房间请求对象
     * @param httpServletRequest HTTP 请求对象 
     * @return 预订房间结果
     */ 
    @PostMapping("/locate")
    public Result<?> locateHotel(@RequestBody LocateRoomRequest locateRoomRequest, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return hotelService.locateRoom(locateRoomRequest, userId);
    }


    /** 
     * 删除酒店 
     * 
     * @param id 酒店主键 ID 
     * @return 操作结果 
     */ 
    @DeleteMapping("/{id}")
    public Result<?> deleteHotel(@PathVariable String id) {

        return hotelService.deleteHotel(Long.parseLong(id));
    }


    /** 
     * 删除房型 
     * 
     * @param id 房型主键 ID 
     * @return 操作结果 
     */ 
    @DeleteMapping("/room/{id}")
    public Result<?> deleteRoom(@PathVariable int id) {

        return hotelService.deleteRoom(id);

    }

    /** 
     * 根据酒店 ID 查询房型列表 
     * 
     * @param id 酒店主键 ID 
     * @return 房型列表 
     */ 
    @GetMapping("/{id}")
    public Result<?> getRoom(@PathVariable String id) {


        return hotelService.getRoom(Long.parseLong(id));
    }


    /** 
     * 根据酒店 ID、房型 ID 和日期查询当日库存 
     * 
     * @param hotelId 酒店主键 ID 
     * @param roomId 房型主键 ID 
     * @param date 查询日期 
     * @return 当日库存信息 
     */ 
    @GetMapping("/{hotelId}/room/{roomId}/{date}")
    public Result<?> getStockByDay(@PathVariable String hotelId, @PathVariable String roomId, @PathVariable String date) {

        return hotelService.getStockByDay(
                Long.parseLong(hotelId), Integer.parseInt(roomId), date
        );
    }

    /** 
     * 根据状态查询酒店列表 
     * 
     * @param getHotelByStatusRequest 按状态查询请求对象 
     * @return 酒店列表 
     */ 
    @PostMapping("/list")
    public Result<?> getHotelByStatus(@RequestBody GetHotelByStatusRequest getHotelByStatusRequest) {

        // status:    0 拒绝 1 正常 2 审核
        return hotelService.getHotelByStatus(getHotelByStatusRequest);
    }

    /** 
     * 分页查询所有酒店 
     * 
     * @param getDataRequest 分页查询请求对象 
     * @return 酒店列表 
     */ 
    @PostMapping("/all")
    public Result<?> getHotels(@RequestBody GetDataRequest getDataRequest) {

        return hotelService.getAllHotels(getDataRequest);
    }

    /** 
     * 审批酒店（通过或拒绝） 
     * 
     * @param id 酒店主键 ID 
     * @param status 审批状态 
     * @return 操作结果 
     */ 
    @PutMapping("/status/{id}")
    public Result<?> approvalHotel(@PathVariable String id, @RequestParam int status) {

        return hotelService.approvalHotel(Long.parseLong(id), status);
    }


    /** 
     * 根据酒店 ID 查询酒店及订单信息 
     * 
     * @param hotelId 酒店主键 ID 
     * @return 酒店及订单信息 
     */ 
    @GetMapping("/getHotel")
    public Result<?> getHotel(@RequestParam String hotelId) {

        return hotelService.getHotelWOrder(Long.parseLong(hotelId));
    }

}
