package com.travelfamilies.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpotResponse {

    private int id;
    private String name;
    private String city;
    private String type;
    private String open_time;
    private int views;
}
