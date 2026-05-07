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
public class RoomRequest {
     private Integer id;

    @NotNull(message = "酒店ID不能为空")
    private Long hotelId;

    @NotBlank(message = "房型名称不能为空")
    private String roomName;

    @NotBlank(message = "床型不能为空")
    private String bedType;

    @NotNull(message = "窗户情况不能为空")
    private int window;

    @NotNull(message = "房间面积不能为空")
    private int area;

    @NotNull(message = "价格不能为空")
    private Double defaultPrice;


    @NotNull(message = "总库存不能为空")
    private int totalInventory;

    private List<String> images;

    @NotNull(message = "状态不能为空")
    private int status;
}
