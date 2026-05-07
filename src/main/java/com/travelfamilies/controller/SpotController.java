package com.travelfamilies.controller;

import com.travelfamilies.request.spotRequest.QuerySpotRequest;
import com.travelfamilies.request.spotRequest.SpotRequest;
import com.travelfamilies.request.spotRequest.UpdateDetailRequest;
import com.travelfamilies.response.Result;
import com.travelfamilies.service.SpotService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
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
    @GetMapping
    public Result<?> getSpot(QuerySpotRequest querySpotRequest) {

        return spotService.getSpot(querySpotRequest);
    }

    @GetMapping("/{id}")
    public Result<?> getSpotDetail(@PathVariable("id") Long id, HttpServletRequest httpServletRequest) {

            return spotService.getSpotDetail(id,httpServletRequest);
    }

    @PostMapping
    public Result<?> addSpot(@RequestBody SpotRequest spotRequest, HttpServletRequest httpServletRequest){

        return spotService.addSpot(spotRequest);
    }

    @PutMapping("/{id}")
    public Result<?> updateSpot(@PathVariable long id,@RequestBody UpdateDetailRequest updateDetailRequest){

        return  spotService.updateSpot(updateDetailRequest,id);
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteSpot(@PathVariable("id") Long id){

        return spotService.deleteSpot(id);
    }
}
