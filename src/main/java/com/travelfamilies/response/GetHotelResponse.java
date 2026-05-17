package com.travelfamilies.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHotelResponse {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private String city;
    private String district;
    private String businessArea;
    private Double basePrice;
    private String mainPic;
}
