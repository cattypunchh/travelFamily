package com.travelfamilies.request.hotelRequest;

public record GetRoomRequest(

        long hotelId,
        String startTime,
        String endTime
) {
}
