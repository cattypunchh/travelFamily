package com.travelfamilies.request.hotelRequest;

import lombok.Data;

@Data
public class GetHotelRequest {

    private int pageNum;
    private int pageSize;
}
