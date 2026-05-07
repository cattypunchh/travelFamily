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

    @PostMapping("/add")
    public Result<?> addComment(@RequestBody AddCommentRequest addCommentRequest, HttpServletRequest httpServletRequest) {

        long userId = (long) httpServletRequest.getAttribute("userID");
        return commentService.addComment(userId, addCommentRequest);
    }


    @GetMapping("/get")
    public Result<?> getComment(@RequestBody GetCommentRequest getCommentRequest) {

        return commentService.getComment(getCommentRequest);

    }

    @GetMapping("/getReply")
    public Result<?> getReplyComment(@RequestBody GetReplyCommentRequest getReplyCommentRequest) {

        return commentService.getReplyComment(getReplyCommentRequest);
    }
}
