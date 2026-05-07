package com.travelfamilies.mapper;

import com.travelfamilies.pojo.Spot;
import com.travelfamilies.request.spotRequest.QuerySpotRequest;
import com.travelfamilies.request.spotRequest.UpdateDetailRequest;
import com.travelfamilies.response.SpotResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface SpotMapper {

    @Select("select id,name,city,type,open_time,views from spot where status=1 order by views desc LIMIT 10")
    List<SpotResponse> getHotSpot();

    List<SpotResponse> getSpot(QuerySpotRequest querySpotRequest);

    @Select("select * from spot where id=#{id}")
    Spot getSpotDetail(Long id);

    @Insert("insert into spot (id,name,city,type,address,price,open_time,description,image_urls,status,views) " +
            "values (#{id},#{name},#{city},#{type},#{address},#{price},#{openTime},#{description},#{imageUrls},#{status},#{views})")
    int addSpot(Spot spot);

    @Update("update spot set price=#{updateDetailRequest.price},open_time=#{updateDetailRequest.openTime}," +
            "description=#{updateDetailRequest.description},image_urls=#{updateDetailRequest.imageUrls} where id=#{id}")
    int updateSpot(UpdateDetailRequest updateDetailRequest,long id);

    @Delete("delete from spot where id=#{id}")
    int deleteSpot(Long id);

    @Update("update spot set views=#{views} where id=#{spotId}")
    void updateViews(Long spotId, int views);

    @Select("select id from spot where name=#{name}")
    Integer getSpotId(String name);

    int updateCount(@Param("list") List<Map<String , Object>> updateList);

    int updateStarRating(@Param("list") List<Map<String, Object>> updateList);
}
