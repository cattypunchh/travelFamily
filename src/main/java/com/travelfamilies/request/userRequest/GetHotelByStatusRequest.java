package com.travelfamilies.request.userRequest;

public record GetHotelByStatusRequest(

        int status,
        int requestPage,
        int requestNum
) {
}
