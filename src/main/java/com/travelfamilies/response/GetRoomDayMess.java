package com.travelfamilies.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRoomDayMess {

    private int roomTypeId;
    private int stock;
    private double price;
}
