package com.travelfamilies.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.travelfamilies.mapper.CommentMapper;
import com.travelfamilies.mapper.ImagesMapper;
import com.travelfamilies.mapper.UserMapper;
import com.travelfamilies.pojo.Comment;
import com.travelfamilies.pojo.Image;
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
    public Result<?> addComment(long userId, AddCommentRequest addCommentRequest) {


        if (!StringUtils.hasText(addCommentRequest.getContent()))
            return Result.failed("请输入评论内容");

        if (addCommentRequest.getCommentId() == 1) {

            if (addCommentRequest.getStarRating() == null) {

                return Result.failed("请评分哦！满分五分");
            }
            addCommentRequest.setParentId(0);
            addCommentRequest.setRootId(0);
        } else {

            /*回复评论不用打分*/
            Comment parentComment = commentMapper.getCommentById(addCommentRequest.getParentId());

            if (parentComment == null) {

                return Result.failed("该条评论已被删除，无法继续评论");
            }

            addCommentRequest.setRootId(parentComment.getRootId() == 0 ? parentComment.getId() : parentComment.getRootId());
        }

        if (commentMapper.addComment(userId, addCommentRequest) == 1) {

            if (addCommentRequest.getRootId() == 0) {

                long targetId = addCommentRequest.getTargetId();

                String scoreKey = addCommentRequest.getTargetType() == 1 ? RedisConstant.COMMENT_SPOT_SCORE : RedisConstant.COMMENT_HOTEL_SCORER;
                String countKey = addCommentRequest.getTargetType() == 1 ? RedisConstant.COMMENT_SPOT_COUNT : RedisConstant.COMMENT_HOTEL_COUNT;

                int result = imagesMapper.addImages(Long.valueOf(addCommentRequest.getId()), 4, addCommentRequest.getImages());
                if (result < 1) {
                    Result.failed("图片上传失败，请重试");
                }
                stringRedisTemplate.opsForValue().increment(countKey + targetId);
                stringRedisTemplate.opsForValue().increment(scoreKey + targetId, addCommentRequest.getStarRating());

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
                .filter(c -> c.getRootId() == 0)
                .sorted(Comparator.comparing(CommentVO::getCreateTime).reversed())
                .toList();

        List<Integer> commentIds =rootComments.stream().map(CommentVO::getId).toList();

        List<Image> images = imagesMapper.getImages(commentIds,4);

        /*ai提供*/
//        Map<Integer, List<String>> imagesGroup = images.stream().
//                collect(Collectors.groupingBy(Image::getComment_id,
//                        Collectors.mapping(Image::getImage_url, Collectors.toList())));
//
//
        Map<Integer,List<String>> imagesGroup=new HashMap<>();

        for(Image image:images){

            int comment_id= Math.toIntExact(image.getTargetId());
            if(!imagesGroup.containsKey(comment_id)){

                imagesGroup.put(comment_id,new ArrayList<>());
            }
            imagesGroup.get(comment_id).add(image.getImageUrl());
        }
        List<CommentVO> repliedComments = commentVOList.stream()
                .filter(f -> f.getRootId() != 0)
                .toList();


        for (CommentVO rootComment : rootComments) {

            List<CommentVO> childComments = repliedComments.stream()
                    .filter(reply -> reply.getRootId().equals(rootComment.getId()))
                    .sorted(Comparator.comparing(CommentVO::getCreateTime).reversed())
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
        List<Comment> commentList = commentMapper.getReplyComment(getReplyCommentRequest.rootId());
        List<CommentVO> commentVOList = getCommentVo(commentList);

        if (commentVOList.isEmpty()) {
            return Result.success("该评论还无人回复");
        }

        PageInfo<CommentVO> pageInfo = new PageInfo<>(commentVOList);
        return Result.success(pageInfo);
    }

    private List<CommentVO> getCommentVo(List<Comment> commentList) {

        Set<Long> userIds = commentList.stream().map(Comment::getUserId).collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<GetUserResponse> users = userMapper.getUser(new ArrayList<>(userIds));
        List<CommentVO> commentVOList = new ArrayList<>();

        Map<Long, GetUserResponse> userMap = new HashMap<>();
        for (GetUserResponse user : users) {
            userMap.put(user.getId(), user);
        }
        for (Comment comment : commentList) {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(comment, commentVO);
            commentVO.setNickname(userMap.get(commentVO.getUserId()).getNickname());
            commentVO.setAvatar(userMap.get(commentVO.getUserId()).getAvatar());
            commentVOList.add(commentVO);
        }
        return commentVOList;
    }
}