package com.travelfamilies.controller;

import com.travelfamilies.request.spotRequest.GetSpotRequest;
import com.travelfamilies.request.spotRequest.QuerySpotRequest;
import com.travelfamilies.request.spotRequest.SpotRequest;
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

    /** 
     * 首页展示热门景点 
     * 
     * @return 热门景点列表 
     */ 
    @GetMapping("/hot")
    public Result<?> getHotSpot() {
        return spotService.getHotSpot();
    }

    /** 
     * 动态搜索景点 
     * 
     * @param querySpotRequest 搜索景点请求对象 
     * @return 搜索结果 
     */ 
    @GetMapping
    public Result<?> getSpot(QuerySpotRequest querySpotRequest) {

        return spotService.getSpot(querySpotRequest);
    }

    /** 
     * 分页查询所有景点 
     * 
     * @param getSpotRequest 分页查询请求对象 
     * @return 景点列表 
     */ 
    @PostMapping("/all")
    public Result<?> getAllSpot(@RequestBody GetSpotRequest getSpotRequest) {

        return spotService.getAllSpot(getSpotRequest);
    }

    /** 
     * 根据景点 ID 查询景点详情 
     * 
     * @param id 景点主键 ID 
     * @param httpServletRequest HTTP 请求对象 
     * @return 景点详情 
     */ 
    @GetMapping("/{id}")
    public Result<?> getSpotDetail(@PathVariable String id, HttpServletRequest httpServletRequest) {

        return spotService.getSpotDetail(Long.parseLong(id), httpServletRequest);
    }

    /** 
     * 添加景点 
     * 
     * @param spotRequest 景点信息请求对象 
     * @return 操作结果 
     */ 
    @PostMapping
    public Result<?> addSpot(@RequestBody SpotRequest spotRequest) {

        return spotService.addSpot(spotRequest);
    }

    /** 
     * 更新景点信息 
     * 
     * @param id 景点主键 ID 
     * @param updateDetailRequest 更新景点详情请求对象 
     * @return 操作结果 
     */ 
    @PutMapping("/{id}")
    public Result<?> updateSpot(@PathVariable String id, @RequestBody UpdateDetailRequest updateDetailRequest) {

        return spotService.updateSpot(updateDetailRequest, Long.parseLong(id));
    }

    /** 
     * 删除景点 
     * 
     * @param id 景点主键 ID 
     * @return 操作结果 
     */ 
    @DeleteMapping("/{id}")
    public Result<?> deleteSpot(@PathVariable String id) {

        return spotService.deleteSpot(Long.valueOf(id));
    }


}
