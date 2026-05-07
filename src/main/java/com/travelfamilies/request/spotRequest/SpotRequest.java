package com.travelfamilies.request.spotRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpotRequest {
    private Long id;

    private String name;

    private String city;

    private String type;

    private String address;

    private BigDecimal price;

    private String openTime;

    private String description;

    private List<String> imageUrls;

    private Integer status;

    private Integer views;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
