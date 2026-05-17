package com.travelfamilies.mapper;

import com.travelfamilies.pojo.User;
import com.travelfamilies.request.userRequest.QueryUserRequest;
import com.travelfamilies.request.userRequest.UpdateDetailRequest;
import com.travelfamilies.request.userRequest.WxProfileRequest;
import com.travelfamilies.response.GetUserResponse;
import com.travelfamilies.response.UserResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Insert("insert into user_detail (id,username,nickname,password,email,role,avatar) values (#{id},#{user.username},#{user.nickname}," +
            "#{user.password},#{user.email},#{user.role},#{user.avatar})")
    void registerUser(User user, Long id);

    @Select("select id,username,password,email,role,status from user_detail where username=#{username}")
    UserResponse getRegisterUser(String username);

    @Select("select password from user_detail where id=#{userId}")
    String getPasswordById(Long userId);

    @Update("update user_detail set password=#{newPassword} where id=#{userId}")
    int setNewPassword(String newPassword, Long userId);

    @Update("update user_detail set phone=#{updateDetailRequest.phone},email=#{updateDetailRequest.email},avatar=#{updateDetailRequest.avatar}," +
            "nickname=#{updateDetailRequest.nickname},gender=#{updateDetailRequest.gender} where id=#{userId}")
    int updateUserDetail(Long userId, UpdateDetailRequest updateDetailRequest);

    @Update("update user_detail set status= 1 - status where id=#{id}")
    int updateStatus(long id);

    List<GetUserResponse> getUser(@Param("ids") List<Long> userIds);

    @Select("select id  from user_detail where username=#{guestName}")
    Long getUserByName(String guestName);

    @Select("select username from user_detail where id=#{id}")
    String getUserName(Long id);

    @Select("select  role from user_detail where id=#{id}")
    int getUserRole(Long id);

    @Select("select id from user_detail where email=#{email}")
    Long getUserByEmail(String email);

    @Select("select * from user_detail")
    List<User> getAllUser();

    List<User> queryUser(QueryUserRequest queryUserRequest);

    @Select("select * from user_detail where openid=#{openid}")
    User getUserByOpenid(String openid);

    @Insert("insert into user_detail (id,username,nickname,password,openid,role,avatar) values " +
            "(#{id},#{username},#{nickname},#{password},#{openid},#{role},#{avatar})")
    int registerWxUser(long id, String username, String password, String nickname, int role, String openid, String avatar);

    @Update("update user_detail set nickname=#{wxProfileRequest.nickname},avatar=#{wxProfileRequest.avatar} where id=#{id}")
    int updateWxProfile(WxProfileRequest wxProfileRequest, long id);
}