package com.travelfamilies.request.hotelRequest;

import jakarta.validation.constraints.NotBlank;

public record UpdateDayMessRequest(


        Integer roomTypeId,
        Long hotelId,
        @NotBlank(message = "要修改的日期不能为空")
        String time,
        Double price,
        int stock) {
}
