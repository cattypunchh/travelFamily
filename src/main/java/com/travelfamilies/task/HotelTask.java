package com.travelfamilies.task;

import com.travelfamilies.mapper.HotelMapper;
import com.travelfamilies.response.GetRoomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class HotelTask {

    private final HotelMapper hotelMapper;
//0/5 * * * * ?
    @Scheduled(cron = "0 0 2 * * *")
    public void dynamicDayStock() {

        LocalDate now = LocalDate.now();
        LocalDate thirtyDay = now.plusDays(30);
        String time = thirtyDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<GetRoomResponse> rooms = hotelMapper.getRoom();
        System.out.println(rooms);
        int resultAdd = hotelMapper.addStockByDay(rooms, time);
        int resultDel=hotelMapper.deleteStockByDay(rooms,time);

        if (resultDel > 0) {
            log.info("更新数据库成功(删除昨天的房型库存)");
        }else{
            log.info("更新数据库失败(删除昨天的房型库存)");
        }

        if (resultAdd > 0) {
            log.info("更新数据库成功(添加第31天的房型库存)");
        }else {
            log.info("更新数据库失败(添加第31天的房型库存)");
        }

    }
}
