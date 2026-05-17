package com.travelfamilies.service;

import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.request.hotelRequest.LocateRoomRequest;
import com.travelfamilies.request.orderRequest.CheckInRequest;
import com.travelfamilies.request.orderRequest.GetOrderRequest;
import com.travelfamilies.request.orderRequest.OrderRequest;
import com.travelfamilies.request.orderRequest.SurePayRequest;
import com.travelfamilies.response.Result;

public interface OrderService {
    Result<?> getRoomDetail(LocateRoomRequest locateRoomRequest, Long userId);

    Result<?> submitOrder(OrderRequest orderRequest, Long userId) throws BusinessException;

    Result<?> surePay(SurePayRequest surePayRequest, Long userId) throws BusinessException;

    Result<?> getOrderSort(Long userId, Integer category);

    Result<?> getOrderByGuest(String guestName);

    Result<?> checkIn(CheckInRequest checkInRequest, Long userId);

    Result<?> checkOut(long orderId, Long userId);

    Result<?> getOrderDetail(long orderId);

    Result<?> getOrder(GetOrderRequest getOrderRequest, long userId);
}
