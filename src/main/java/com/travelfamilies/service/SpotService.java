package com.travelfamilies.service;

import com.travelfamilies.pojo.Spot;
import com.travelfamilies.request.spotRequest.QuerySpotRequest;
import com.travelfamilies.request.spotRequest.UpdateDetailRequest;
import com.travelfamilies.response.Result;
import jakarta.servlet.http.HttpServletRequest;

public interface SpotService {

    Result<?> getHotSpot();

    Result<?> getSpot(QuerySpotRequest querySpotRequest);

    Result<?> getSpotDetail(int id, HttpServletRequest httpServletRequest);

    Result<?> addSpot(Spot spot);

    Result<?> updateSpot(UpdateDetailRequest updateDetailRequest);

    Result<?> deleteSpot(int id);
}
