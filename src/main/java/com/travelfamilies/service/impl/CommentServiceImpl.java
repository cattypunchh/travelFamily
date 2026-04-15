package com.travelfamilies.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.travelfamilies.mapper.CommentMapper;
import com.travelfamilies.mapper.ImagesMapper;
import com.travelfamilies.mapper.UserMapper;
import com.travelfamilies.pojo.Comment;
import com.travelfamilies.pojo.CommentImage;
import com.travelfamilies.pojo.CommentVO;
import com.travelfamilies.request.commentRequest.AddCommentRequest;
import com.travelfamilies.request.commentRequest.GetCommentRequest;
import com.travelfamilies.request.commentRequest.GetReplyCommentRequest;
import com.travelfamilies.response.GetUserResponse;
import com.travelfamilies.response.Result;
import com.travelfamilies.service.CommentService;
import com.travelfamilies.tools.RedisConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ImagesMapper imagesMapper;

    @Override
    public Result<?> addComment(int userId, AddCommentRequest addCommentRequest) {


        if (!StringUtils.hasText(addCommentRequest.getContent()))
            return Result.failed("请输入评论内容");

        if (addCommentRequest.getCommentId() == 1) {
            addCommentRequest.setParent_id(0);
            addCommentRequest.setRoot_id(0);

            if (addCommentRequest.getStar_rating() == null) {

                return Result.failed("请评分哦！满分五分");
            }
        } else {

            /*回复评论不用打分*/
            Comment parentComment = commentMapper.getCommentById(addCommentRequest.getParent_id());

            if (parentComment == null) {

                return Result.failed("该条评论已被删除，无法继续评论");
            }

            addCommentRequest.setRoot_id(parentComment.getRoot_id() == 0 ? parentComment.getId() : parentComment.getRoot_id());
        }

        if (commentMapper.addComment(userId, addCommentRequest) == 1) {

            if (addCommentRequest.getRoot_id() == 0) {

                int targetId = addCommentRequest.getTarget_id();

                String scoreKey = addCommentRequest.getTarget_type() == 1 ? RedisConstant.COMMENT_SPOT_SCORE : RedisConstant.COMMENT_HOTEL_SCORER;
                String countKey = addCommentRequest.getTarget_type() == 1 ? RedisConstant.COMMENT_SPOT_COUNT : RedisConstant.COMMENT_HOTEL_COUNT;

                int result = commentMapper.addImages(addCommentRequest.getId(), addCommentRequest.getImages());
                if (result < 1) {
                    Result.failed("图片上传失败，请重试");
                }
                stringRedisTemplate.opsForValue().increment(countKey + targetId);
                stringRedisTemplate.opsForValue().increment(scoreKey + targetId, addCommentRequest.getStar_rating());

            }

            return Result.success(" 评论成功");
        }
        return Result.failed("评论失败，请重试");
    }

    @Override
    public Result<?> getComment(GetCommentRequest getCommentRequest) {

        PageHelper.startPage(getCommentRequest.requestPage(), getCommentRequest.requestNum());
        List<Comment> commentList = commentMapper.getComment(getCommentRequest);
        if (commentList == null || commentList.isEmpty()) {

            return Result.success("暂无评论");
        }
        List<CommentVO> commentVOList = getCommentVo(commentList);
        /*得到每个评论的用户名及头像
        考虑是用map内存还是Redis*/
        List<CommentVO> rootComments = commentVOList.stream()
                .filter(c -> c.getRoot_id() == 0)
                .sorted(Comparator.comparing(CommentVO::getCreate_time).reversed())
                .toList();

        List<Integer> commentIds = rootComments.stream().map(CommentVO::getId).toList();
        List<CommentImage> images = imagesMapper.getImages(commentIds);

        /*ai提供*/
//        Map<Integer, List<String>> imagesGroup = images.stream().
//                collect(Collectors.groupingBy(CommentImage::getComment_id,
//                        Collectors.mapping(CommentImage::getImage_url, Collectors.toList())));
//
//
        Map<Integer,List<String>> imagesGroup=new HashMap<>();

        for(CommentImage commentImage:images){

            int comment_id=commentImage.getComment_id();
            if(!imagesGroup.containsKey(comment_id)){

                imagesGroup.put(comment_id,new ArrayList<>());
            }
            imagesGroup.get(comment_id).add(commentImage.getImage_url());
        }
        List<CommentVO> repliedComments = commentVOList.stream()
                .filter(f -> f.getRoot_id() != 0)
                .toList();


        for (CommentVO rootComment : rootComments) {

            List<CommentVO> childComments = repliedComments.stream()
                    .filter(reply -> reply.getRoot_id().equals(rootComment.getId()))
                    .sorted(Comparator.comparing(CommentVO::getCreate_time).reversed())
                    .collect(Collectors.toList());

            rootComment.setImages(imagesGroup.get(rootComment.getId()));
            rootComment.setChildren(childComments);
        }
        PageInfo<CommentVO> pageInfo = new PageInfo<>(rootComments);
        return Result.success(pageInfo);
    }

    @Override
    public Result<?> getReplyComment(GetReplyCommentRequest getReplyCommentRequest) {

        PageHelper.startPage(getReplyCommentRequest.pageNum(), getReplyCommentRequest.pageSize());
        List<Comment> commentList = commentMapper.getReplyComment(getReplyCommentRequest.root_id());
        List<CommentVO> commentVOList = getCommentVo(commentList);

        if (commentVOList.isEmpty()) {
            return Result.success("该评论还无人回复");
        }

        PageInfo<CommentVO> pageInfo = new PageInfo<>(commentVOList);
        return Result.success(pageInfo);
    }

    private List<CommentVO> getCommentVo(List<Comment> commentList) {

        Set<Integer> userIds = commentList.stream().map(Comment::getUser_id).collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<GetUserResponse> users = userMapper.getUser(new ArrayList<>(userIds));
        List<CommentVO> commentVOList = new ArrayList<>();

        Map<Integer, GetUserResponse> userMap = new HashMap<>();
        for (GetUserResponse user : users) {
            userMap.put(user.getId(), user);
        }
        for (Comment comment : commentList) {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(comment, commentVO);
            commentVO.setNickname(userMap.get(commentVO.getUser_id()).getNickname());
            commentVO.setAvatar(userMap.get(commentVO.getUser_id()).getAvatar());
            commentVOList.add(commentVO);
        }
        return commentVOList;
    }
}