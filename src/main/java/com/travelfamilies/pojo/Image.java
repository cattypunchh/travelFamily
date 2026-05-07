package com.travelfamilies.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {

    private int id;
    private long targetId;
    private int targetType;
    private String imageUrl;
    private int isMain;
    private int sortOrder;
    private LocalDateTime createTime;
}
