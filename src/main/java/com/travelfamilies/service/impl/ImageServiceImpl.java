package com.travelfamilies.service.impl;

import com.travelfamilies.mapper.ImagesMapper;
import com.travelfamilies.pojo.Image;
import com.travelfamilies.response.Result;
import com.travelfamilies.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImagesMapper imagesMapper;

    @Override
    public Result<?> getAllImages(long id, int type) {

        List<Object> ids = new ArrayList<>();
        ids.add(id);
        List<Image> images = imagesMapper.getImages(ids, type);

        return Result.success(images);
    }
}
