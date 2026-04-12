package com.travelfamilies.mapper;

import com.travelfamilies.pojo.Spot;
import com.travelfamilies.request.spotRequest.QuerySpotRequest;
import com.travelfamilies.request.spotRequest.UpdateDetailRequest;
import com.travelfamilies.response.SpotResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpotMapper {

    @Select("select id,name,city,type,open_time,views from spot where status=1 order by views desc LIMIT 10")
    List<SpotResponse> getHotSpot();


    List<SpotResponse> getSpot(QuerySpotRequest querySpotRequest);


    @Select("select * from spot where id=#{id}")
    Spot getSpotDetail(int id);

    @Insert("insert into spot (name,city,type,address,price,open_time,description,image_urls,status,views) " +
            "values (#{name},#{city},#{type},#{address},#{price},#{open_time},#{description},#{image_urls},#{status},#{views})")
    int addSpot(Spot spot);

    @Update("update spot set price=#{price},open_time=#{open_time},description=#{description},image_urls=#{image_urls} where id=#{id}")
    int updateSpot(UpdateDetailRequest updateDetailRequest);

    @Delete("delete from spot where id=#{id}")
    int deleteSpot(int id);

    @Update("update spot set views=#{views} where id=#{spotId}")
    void updateViews(int spotId, int views);
}
