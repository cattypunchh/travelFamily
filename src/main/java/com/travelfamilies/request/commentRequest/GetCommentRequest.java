package com.travelfamilies.request.commentRequest;

public record GetCommentRequest(

        int target_type,
        int target_id,
        int requestPage,
        int requestNum
){
}
