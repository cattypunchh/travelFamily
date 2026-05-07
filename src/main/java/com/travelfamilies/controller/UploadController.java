package com.travelfamilies.controller;

import com.travelfamilies.response.Result;
import com.travelfamilies.tools.AliOSSUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final AliOSSUtils aliOSSUtils;

    @PostMapping
    public Result<?> upload(@RequestParam MultipartFile file) throws IOException {

        String url = aliOSSUtils.upload(file);

        return Result.success(url);
    }
}
