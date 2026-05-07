package com.travelfamilies.request.hotelRequest;

public record LocateRoomRequest(
        Long hotelId,
        int roomId,
        String startTime,
        String endTime
) {
}
