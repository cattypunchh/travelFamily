package com.travelfamilies.request.commentRequest;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCommentRequest{

    /*1.根评论 2.子评论
    * 根据前端点击哪个按钮来确定的*/
    Integer id;
    int commentId;
    long targetId;
    int targetType;
    String content;
    Integer starRating;
    Integer parentId;
    Integer rootId;
    List<String> images;


}
