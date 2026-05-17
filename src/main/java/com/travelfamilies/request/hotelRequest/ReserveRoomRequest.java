package com.travelfamilies.request.hotelRequest;


public record ReserveRoomRequest(
        String city,
        String startTime,
        String endTime

) {
}
