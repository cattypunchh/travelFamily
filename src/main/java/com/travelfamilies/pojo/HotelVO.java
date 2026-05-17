package com.travelfamilies.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelVO {

    List<Image> images;
    List<Room> rooms;
    List<Coupon> coupons;
    private String name;
    private String city;
    private String district;
    private String businessArea;
    private String address;
    private String phone;
    private String description;
    private int starLevel;
}
