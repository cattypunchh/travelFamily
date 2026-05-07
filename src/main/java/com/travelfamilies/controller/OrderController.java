package com.travelfamilies.controller;

import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.request.hotelRequest.LocateRoomRequest;
import com.travelfamilies.request.orderRequest.CheckInRequest;
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

    @GetMapping("orderInfo")
    public Result<?> getOrderInfo(@RequestBody LocateRoomRequest locateRoomRequest, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return orderService.getRoomDetail(locateRoomRequest, userId);
    }

    @GetMapping("/submit")
    public Result<?> submit(@RequestBody OrderRequest orderRequest, HttpServletRequest httpServletRequest) throws BusinessException {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return orderService.submitOrder(orderRequest, userId);
    }

    @GetMapping("/surePay")
    public Result<?> surePay(@RequestBody SurePayRequest surePayRequest, HttpServletRequest httpServletRequest) throws BusinessException {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return orderService.surePay(surePayRequest, userId);
    }

    @GetMapping("/getOrderSort")
    public Result<?> getOrderSort(@RequestParam Integer category, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return orderService.getOrderSort(userId, category);
    }


    @PostMapping("/getOrderByGuest")
    public Result<?> getOrderByGuest(@RequestParam String guestName, HttpServletRequest httpServletRequest) {

        return orderService.getOrderByGuest(guestName);
    }

    @PostMapping("/checkIn")
    public Result<?> checkIn(@RequestBody CheckInRequest checkInRequest, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");

        return orderService.checkIn(checkInRequest,userId);
    }

    @PostMapping("/checkOut")
    public Result<?> checkOut(@RequestParam long recordId, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return orderService.checkOut(recordId,userId);
    }

}
