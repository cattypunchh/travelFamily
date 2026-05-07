package com.travelfamilies.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    private int id;
    private Long userId;
    private Long targetId;
    private int targetType;
    private String content;
    private Integer starRating;
    private Integer parentId;
    private Integer rootId;
    private int status;
    List<String> images;
    private LocalDateTime createTime;

}
