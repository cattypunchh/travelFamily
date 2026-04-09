package com.travelfamilies.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private int id;
    private String username;
    private String password;
    private String email;
    private int status;
    private int role;


}
