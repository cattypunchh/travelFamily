package com.travelfamilies.request.hotelRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelRequest {

    @NotBlank(message = "名称不能为空")
    private String name;

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "区县不能为空")
    private String district;

    @NotBlank(message = "商圈不能为空")
    private String businessArea;

    @NotBlank(message = "详细地址不能为空")
    private String address;

    @NotBlank(message = "描述信息不能为空")
    private String description;

    @NotNull(message = "星级/评分不能为空")
    private int starLevel;

    @NotNull(message = "起步价格不能为空")
    private Double basePrice;

    @NotBlank(message = "图片不能为空")
    private List<String> images;

    @NotNull(message = "状态不能为空")
    private int status;

}
