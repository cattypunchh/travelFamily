package com.travelfamilies.request.orderRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToEmptyObjectSerializer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInRequest {

    @JsonSerialize(using = ToEmptyObjectSerializer.class)
    private long orderId;
    private String roomNumber;
    private String idCard;

}
