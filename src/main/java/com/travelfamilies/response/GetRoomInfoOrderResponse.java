package com.travelfamilies.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRoomInfoOrderResponse {


    private int id;
    private String hotelId;
    private String roomName;
    private String bedType;
}
