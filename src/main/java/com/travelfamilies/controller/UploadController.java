package com.travelfamilies.controller;

import com.travelfamilies.response.Result;
import com.travelfamilies.tools.AliOSSUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final AliOSSUtils aliOSSUtils;

    /** 
     * 上传文件到阿里云 OSS 
     * 
     * @param file 上传的文件 
     * @return 文件访问 URL 
     * @throws IOException IO 异常 
     */ 
    @PostMapping
    public Result<?> upload(@RequestParam MultipartFile file) throws IOException {

        String url = aliOSSUtils.upload(file);

        return Result.success(url);
    }
}
