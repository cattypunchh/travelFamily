package com.travelfamilies.request.orderRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInRequest {

    private long orderId;
    private String roomNumber;
    private String idCard;

}
