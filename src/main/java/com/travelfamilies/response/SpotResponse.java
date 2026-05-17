package com.travelfamilies.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpotResponse {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private String city;
    private String type;
    private String openTime;
    private int views;
    private String imageUrls;


}
