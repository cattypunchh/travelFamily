package com.travelfamilies.mapper;

import com.travelfamilies.pojo.Comment;
import com.travelfamilies.request.commentRequest.AddCommentRequest;
import com.travelfamilies.request.commentRequest.GetCommentRequest;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Insert("insert into comment (user_id,target_id,target_type,content,star_rating,parent_id,root_id)" +
            "values (#{userId},#{addCommentRequest.targetId},#{addCommentRequest.targetType},#{addCommentRequest.content}," +
            "#{addCommentRequest.starRating},#{addCommentRequest.parentId},#{addCommentRequest.rootId})")
    @Options(useGeneratedKeys = true, keyProperty = "addCommentRequest.id", keyColumn = "id")
    int addComment(long userId, AddCommentRequest addCommentRequest);

    @Select("select * from comment where  id=#{parentId}")
    @Options(useGeneratedKeys = true, keyProperty = "Comment.id", keyColumn = "id")
    Comment getCommentById(Integer parentId);

    @Select("select * from comment where target_id=#{targetId} and target_type=#{targetType}")
    @Options(useGeneratedKeys = true, keyProperty = "Comment.id", keyColumn = "id")
    List<Comment> getComment(GetCommentRequest getCommentRequest);

    @Select("select * from comment where root_id=#{rootId}")
    List<Comment> getReplyComment(int rootId);

    @Select("select id from comment where target_id=#{orderId} and user_id=#{userId} and target_type=3")
    Integer getCommentStatus(long orderId, long userId);
}
