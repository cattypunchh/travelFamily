package com.travelfamilies.tools;

public final class RedisConstant {

    /*登录用户的token*/
    public static final String USER_TOKEN="login:user:token:";
    /*登录管理员的token*/
    public static final String ADMIN_TOKEN="login:admin:token:";
    /*处于黑名单的用户*/
    public static final String USER_BLACK_LIST="blacklist:user:";
    /*处于黑名单的管理员*/
    public static final String ADMIN_BLACK_LIST="blacklist:admin:";
    /*热点spot 前十名*/
    public static final String SPOT_TOP10_LIST="spot";
    /*spot详情*/
    public static final String SPOT_TYPE_DETAIL="spot:type:detail";
    /*某个文章的浏览量*/
    public static final String SPOT_VIEWS="spot:views:";
    /*防止某个用户恶意刷某景点浏览量*/
    public static final String SPOT_VIEWS_USER="spot:views:user:";


    public static final Long SPOT_VIEWS_USER_EXPIRES=30*60*1000L;
    public static final Long TOKEN_EXPIRES_TIME=24*3600*1000L;
}
