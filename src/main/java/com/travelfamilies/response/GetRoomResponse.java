package com.travelfamilies.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRoomResponse {

    private Integer hotelId;
    private Integer id;
    private Double defaultPrice;
    private int totalInventory;
    private int status;
}
