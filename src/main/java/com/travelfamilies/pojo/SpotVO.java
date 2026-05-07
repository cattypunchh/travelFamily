package com.travelfamilies.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpotVO {
    private Long id;

    private String name;

    private String city;

    private String type;

    private String address;

    private BigDecimal price;

    private String openTime;

    private String description;

    private List<Image> imageUrls;

    private Integer status;

    private Integer views;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
