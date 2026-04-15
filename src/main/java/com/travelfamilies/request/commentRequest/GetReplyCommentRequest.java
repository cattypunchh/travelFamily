package com.travelfamilies.request.commentRequest;

public record GetReplyCommentRequest(int root_id,
                                     int pageNum,
                                     int pageSize) {

}
