package com.travelfamilies.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpotResponse {

    private Long id;
    private String name;
    private String city;
    private String type;
    private String openTime;
    private int views;
}
