package com.travelfamilies.service;

import com.travelfamilies.request.spotRequest.QuerySpotRequest;
import com.travelfamilies.request.spotRequest.SpotRequest;
import com.travelfamilies.request.spotRequest.UpdateDetailRequest;
import com.travelfamilies.response.Result;
import jakarta.servlet.http.HttpServletRequest;

public interface SpotService {

    Result<?> getHotSpot();

    Result<?> getSpot(QuerySpotRequest querySpotRequest);

    Result<?> getSpotDetail(Long id, HttpServletRequest httpServletRequest);

    Result<?> addSpot(SpotRequest spotRequest);

    Result<?> updateSpot(UpdateDetailRequest updateDetailRequest,long id);

    Result<?> deleteSpot(Long id);
}
