package com.travelfamilies.tools;


import com.travelfamilies.mapper.HotelMapper;
import com.travelfamilies.pojo.HotelOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CalculateDays {


    private final  HotelMapper hotelMapper;
    public Map<String,Object> calculate(HotelOrder hotelOrder) {


        Map<String,Object> result = new HashMap<>();
        String startTime = hotelOrder.getCheckInDate();
        LocalDate end = LocalDate.parse(hotelOrder.getCheckOutDate());
        LocalDate start = LocalDate.parse(startTime);
        long days = ChronoUnit.DAYS.between(start, end);
        result.put("days",days);
        String localDays = String.valueOf(end.minusDays(1));
        result.put("result",hotelMapper.rollBackStock(hotelOrder.getRoomId(), startTime, localDays));

        return result;
    }

}
