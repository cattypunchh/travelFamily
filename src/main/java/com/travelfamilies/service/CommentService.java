package com.travelfamilies.service;

import com.travelfamilies.request.commentRequest.AddCommentRequest;
import com.travelfamilies.request.commentRequest.GetCommentRequest;
import com.travelfamilies.request.commentRequest.GetReplyCommentRequest;
import com.travelfamilies.response.Result;

public interface CommentService {
    Result<?> addComment(int userId, AddCommentRequest addCommentRequest);

    Result<?> getComment(GetCommentRequest getCommentRequest);

    Result<?> getReplyComment(GetReplyCommentRequest getReplyCommentRequest);
}
