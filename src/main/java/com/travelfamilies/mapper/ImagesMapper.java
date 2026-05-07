package com.travelfamilies.mapper;

import com.travelfamilies.pojo.Image;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ImagesMapper {

    List<Image> getImages(@Param("commentIds") List<?> commentIds,@Param("type") int type);

    int addImages(@Param("targetId") Long targetId,
                  @Param("targetType") Integer targetType,
                  @Param("images") List<String> images);

}
