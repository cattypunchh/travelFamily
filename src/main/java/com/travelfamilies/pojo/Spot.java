package com.travelfamilies.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Spot {

    private Integer id;

    private String name;

    private String city;

    private String type;

    private String address;

    private BigDecimal price;

    private String open_time;

    private String description;

    private String image_urls;

    private Integer status;

    private Integer views;

    private LocalDateTime create_time;

    private LocalDateTime update_time;
}
