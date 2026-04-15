package com.travelfamilies.mapper;

import com.travelfamilies.pojo.CommentImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface ImagesMapper {


    List<CommentImage> getImages(@Param("commentIds") List<Integer> commentIds);
}
