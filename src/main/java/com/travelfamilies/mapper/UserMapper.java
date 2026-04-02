package com.travelfamilies.mapper;

import com.travelfamilies.request.userRequest.UpdateDetailRequest;
import com.travelfamilies.response.UserResponse;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Insert("insert into user_detail (username,nickname,password,email) values (#{username},#{nickname},#{password},#{email})")
    void registerUser(String username, String nickname, String password, String email);

    @Select("select id,username,password,email from user_detail where username=#{username}")
    UserResponse getRegisterUser(String username);

    @Select("select password from user_detail where id=#{userId}")
    String getPasswordById(int userId);

    @Update("update user_detail set password=#{newPassword} where id=#{userId}")
    int setNewPassword(String newPassword, int userId);

    @Update("update user_detail set phone=#{updateDetailRequest.phone},email=#{updateDetailRequest.email},avatar=#{updateDetailRequest.avatar},nickname=#{updateDetailRequest.nickname},gender=#{updateDetailRequest.gender} where id=#{userId}")
    int updateUserDetail(int userId, UpdateDetailRequest updateDetailRequest);
}