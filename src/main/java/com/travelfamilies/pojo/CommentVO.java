package com.travelfamilies.pojo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentVO {
    private int id;
    private int user_id;
    private String avatar;
    private String nickname;
    private String content;
    private Integer star_rating;
    private Integer parent_id;
    private Integer root_id;
    List<String> images;
    private LocalDateTime create_time;
    private List<CommentVO> children = new ArrayList<>();
}
