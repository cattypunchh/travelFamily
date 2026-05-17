package com.travelfamilies.controller;

import com.travelfamilies.response.Result;
import com.travelfamilies.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;


    /** 
     * 根据关联 ID 和类型查询所有图片 
     * 
     * @param id 关联主键 ID 
     * @param type 图片类型 
     * @return 图片列表 
     */ 
    @GetMapping
    public Result<?> getAllImages(@RequestParam long id, int type) {

        return imageService.getAllImages(id, type);
    }
}
