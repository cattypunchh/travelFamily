package com.travelfamilies.request.commentRequest;

public record GetReplyCommentRequest(int rootId,
                                     int pageNum,
                                     int pageSize) {

}
