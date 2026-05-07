package com.travelfamilies.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String avatar;
    private String nickname;
    private int gender;
    private int status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer role=1;
}
