package com.travelfamilies.controller;


import com.travelfamilies.request.commentRequest.AddCommentRequest;
import com.travelfamilies.request.commentRequest.GetCommentRequest;
import com.travelfamilies.request.commentRequest.GetReplyCommentRequest;
import com.travelfamilies.response.Result;
import com.travelfamilies.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /** 
     * 添加评论 
     * 
     * @param addCommentRequest 添加评论请求对象 
     * @param httpServletRequest HTTP 请求对象 
     * @return 操作结果 
     */ 
    @PostMapping("/add")
    public Result<?> addComment(@RequestBody AddCommentRequest addCommentRequest, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return commentService.addComment(userId, addCommentRequest);
    }


    /** 
     * 查询评论列表 
     * 
     * @param getCommentRequest 查询评论请求对象 
     * @return 评论列表 
     */ 
    @PostMapping("/get")
    public Result<?> getComment(@RequestBody GetCommentRequest getCommentRequest) {

        return commentService.getComment(getCommentRequest);

    }

    /** 
     * 查询回复评论 
     * 
     * @param getReplyCommentRequest 查询回复评论请求对象 
     * @return 回复评论列表 
     */ 
    @PostMapping("/getReply")
    public Result<?> getReplyComment(@RequestBody GetReplyCommentRequest getReplyCommentRequest) {

        return commentService.getReplyComment(getReplyCommentRequest);
    }

    /** 
     * 查询订单评论状态 0 or 1
     * 
     * @param orderId 订单主键 ID 
     * @param httpServletRequest HTTP 请求对象 
     * @return 评论状态 
     */ 
    @GetMapping("/getCommentStatus")
    public Result<?> getCommentStatus(@RequestParam String orderId, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return commentService.getCommentStatus(Long.parseLong(orderId), userId);
    }
}
