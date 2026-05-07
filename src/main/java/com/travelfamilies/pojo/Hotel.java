package com.travelfamilies.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hotel {

    private Long id;
    private String name;
    private String city;
    private String district;
    private String businessArea;
    private String address;
    private String description;
    private int starLevel;
    private Double basePrice;
    private String mainPic;
    private int status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
