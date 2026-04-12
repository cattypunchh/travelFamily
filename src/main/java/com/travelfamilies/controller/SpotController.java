package com.travelfamilies.controller;

import com.travelfamilies.pojo.Spot;
import com.travelfamilies.request.spotRequest.QuerySpotRequest;
import com.travelfamilies.request.spotRequest.UpdateDetailRequest;
import com.travelfamilies.response.Result;
import com.travelfamilies.service.SpotService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spot")
@RequiredArgsConstructor
public class SpotController {

    private final SpotService spotService;

    /*
     * 首页展示热门景点
     * */
    @GetMapping("/hot")
    public Result<?> getHotSpot(){

        return spotService.getHotSpot();
    }

    /*
    * 此搜索功能不是很好
    * 应该更加动态
    *
    * (已替换称更加动态的搜索）*/
    @GetMapping("")
    public Result<?> getSpot(QuerySpotRequest querySpotRequest) {

        return spotService.getSpot(querySpotRequest);
    }

    @GetMapping("/detail")
    public Result<?> getSpotDetail(@RequestParam int id,HttpServletRequest httpServletRequest) {

            return spotService.getSpotDetail(id,httpServletRequest);
    }

    @PostMapping("/add")
    public Result<?> addSpot(@RequestBody Spot spot,HttpServletRequest httpServletRequest){

        return spotService.addSpot(spot);
    }

    @PutMapping("/update")
    public Result<?> updateSpot(@RequestBody UpdateDetailRequest updateDetailRequest,HttpServletRequest httpServletRequest){

        return  spotService.updateSpot(updateDetailRequest);
    }

    @PutMapping("/delete")
    public Result<?> deleteSpot(@RequestParam int id){

        return spotService.deleteSpot(id);
    }
}
