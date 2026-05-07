package com.travelfamilies.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHotelResponse {

    private Long id;
    private String name;
    private String city;
    private String district;
    private String businessArea;
    private Double basePrice;
    private String mainPic;
}
