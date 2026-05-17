package com.travelfamilies.pojo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentVO {
    List<String> images;
    private int id;
    private Long userId;
    private String avatar;
    private Long targetId;
    private int targetType;
    private String nickname;
    private String content;
    private Integer starRating;
    private Integer parentId;
    private Integer rootId;
    private LocalDateTime createTime;
    private List<CommentVO> children = new ArrayList<>();
}
