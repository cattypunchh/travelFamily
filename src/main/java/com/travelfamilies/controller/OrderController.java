package com.travelfamilies.controller;

import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.request.hotelRequest.LocateRoomRequest;
import com.travelfamilies.request.orderRequest.CheckInRequest;
import com.travelfamilies.request.orderRequest.GetOrderRequest;
import com.travelfamilies.request.orderRequest.OrderRequest;
import com.travelfamilies.request.orderRequest.SurePayRequest;
import com.travelfamilies.response.Result;
import com.travelfamilies.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /** 
     * 获取订单信息
     * 
     * @param locateRoomRequest 定位房间请求对象 
     * @param httpServletRequest HTTP 请求对象 
     * @return 订单房间详情 
     */ 
    @PostMapping("orderInfo")
    public Result<?> getOrderInfo(@RequestBody LocateRoomRequest locateRoomRequest, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return orderService.getRoomDetail(locateRoomRequest, userId);
    }

    /** 
     * 提交订单 
     * 
     * @param orderRequest 订单请求对象 
     * @param httpServletRequest HTTP 请求对象 
     * @return 操作结果 
     * @throws BusinessException 业务异常 
     */ 
    @PostMapping("/submit")
    public Result<?> submit(@RequestBody OrderRequest orderRequest, HttpServletRequest httpServletRequest) throws BusinessException {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return orderService.submitOrder(orderRequest, userId);
    }


    /** 
     * 根据订单 ID 查询订单详情 
     * 
     * @param orderId 订单主键 ID 
     * @return 订单详情 
     */ 
    @GetMapping("/{id}")
    public Result<?> getOrderDetail(@PathVariable("id") String orderId) {

        return orderService.getOrderDetail(Long.parseLong(orderId));
    }

    /** 
     * 确认支付 
     * 
     * @param surePayRequest 确认支付请求对象 
     * @param httpServletRequest HTTP 请求对象 
     * @return 操作结果 
     * @throws BusinessException 业务异常 
     */ 
    @PostMapping("/surePay")
    public Result<?> surePay(@RequestBody SurePayRequest surePayRequest, HttpServletRequest httpServletRequest) throws BusinessException {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return orderService.surePay(surePayRequest, userId);
    }

    /** 
     * 按分类获取订单列表 
     * 
     * @param category 订单分类 
     * @param httpServletRequest HTTP 请求对象 
     * @return 订单列表 
     */ 
    @GetMapping("/getOrderSort")
    public Result<?> getOrderSort(@RequestParam Integer category, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return orderService.getOrderSort(userId, category);
    }


    /** 
     * 根据住客姓名查询订单 
     * 
     * @param guestName 住客姓名 
     * @return 订单信息 
     */ 
    @PostMapping("/getOrderByGuest")
    public Result<?> getOrderByGuest(@RequestParam String guestName) {

        return orderService.getOrderByGuest(guestName);
    }

    /** 
     * 办理入住 
     * 
     * @param checkInRequest 入住请求对象 
     * @param httpServletRequest HTTP 请求对象 
     * @return 操作结果 
     */ 
    @PostMapping("/checkIn")
    public Result<?> checkIn(@RequestBody CheckInRequest checkInRequest, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");

        return orderService.checkIn(checkInRequest, userId);
    }

    /** 
     * 办理退房 
     * 
     * @param orderId 订单主键 ID 
     * @param httpServletRequest HTTP 请求对象 
     * @return 操作结果 
     */ 
    @PostMapping("/checkOut")
    public Result<?> checkOut(@RequestParam String orderId, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return orderService.checkOut(Long.parseLong(orderId), userId);
    }


    /** 
     * 管理员分页查询订单
     * 
     * @param getOrderRequest 查询订单请求对象 
     * @param httpServletRequest HTTP 请求对象 
     * @return 订单列表 
     */ 
    @PostMapping("/getOrder")
    public Result<?> getOrder(@RequestBody GetOrderRequest getOrderRequest, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return orderService.getOrder(getOrderRequest, userId);
    }
}
