package com.travelfamilies.service;

import com.travelfamilies.response.Result;

public interface ImageService {
    Result<?> getAllImages(long id, int type);
}
