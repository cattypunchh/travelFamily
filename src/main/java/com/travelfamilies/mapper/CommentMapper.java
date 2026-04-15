package com.travelfamilies.mapper;

import com.travelfamilies.pojo.Comment;
import com.travelfamilies.request.commentRequest.AddCommentRequest;
import com.travelfamilies.request.commentRequest.GetCommentRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Insert("insert into comment (user_id,target_id,target_type,content,star_rating,parent_id,root_id)" +
            "values (#{userId},#{addCommentRequest.target_id},#{addCommentRequest.target_type},#{addCommentRequest.content}," +
            "#{addCommentRequest.star_rating},#{addCommentRequest.parent_id},#{addCommentRequest.root_id})")
    @Options(useGeneratedKeys = true, keyProperty = "addCommentRequest.id", keyColumn = "id")
    int addComment(int userId,AddCommentRequest addCommentRequest);

    @Select("select * from comment where  id=#{parentId}")
    @Options(useGeneratedKeys = true, keyProperty = "Comment.id", keyColumn = "id")
    Comment getCommentById(Integer parentId);

    @Select("select * from comment where target_id=#{target_id} and target_type=#{target_type}")
    @Options(useGeneratedKeys = true, keyProperty = "Comment.id", keyColumn = "id")
    List<Comment> getComment(GetCommentRequest getCommentRequest);

    @Select("select * from comment where root_id=#{rootId}")
    List<Comment> getReplyComment(int rootId);


    int addImages(@Param("commentId") int commentId,@Param("images") List<String> images);
}
