package com.travelfamilies.request.hotelRequest;

import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

public record GetRoomRequest(

        @JsonSerialize(using = ToStringSerializer.class)
        long hotelId,
        String startTime,
        String endTime
) {
}
