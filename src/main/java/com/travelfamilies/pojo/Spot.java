package com.travelfamilies.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Spot {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String name;

    private String city;

    private String type;

    private String address;

    private BigDecimal price;

    private String openTime;

    private String description;

    private String imageUrls;

    private Integer status;

    private Integer views;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
