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
    private int user_id;
    private int target_id;
    private int target_type;
    private String content;
    private Integer star_rating;
    private Integer parent_id;
    private Integer root_id;
    private int status;
    List<String> images;
    private LocalDateTime create_time;

}
