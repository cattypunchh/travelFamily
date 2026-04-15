package com.travelfamilies.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentImage {

    private int id;
    private int comment_id;
    private String image_url;
    private int sort_order;
    private LocalDateTime create_time;
}
