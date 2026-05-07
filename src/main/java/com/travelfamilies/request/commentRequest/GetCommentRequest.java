package com.travelfamilies.request.commentRequest;

public record GetCommentRequest(

        int targetType,
        long targetId,
        int requestPage,
        int requestNum
){
}
