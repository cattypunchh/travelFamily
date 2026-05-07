package com.travelfamilies.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    private Integer id;

    private long hotelId;

    private String roomName;

    private String bedType;

    private int window;

    private int area;

    private Double defaultPrice;

    private int totalInventory;

    private String images;

    private int status;
}
