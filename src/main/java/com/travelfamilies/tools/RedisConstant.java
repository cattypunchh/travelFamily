package com.travelfamilies.tools;

public final class RedisConstant {

    public static final String USER_TOKEN="login:user:token:";
    public static final String ADMIN_TOKEN="login:admin:token:";
    public static final String USER_BLACK_LIST="blacklist:user:";
    public static final String ADMIN_BLACK_LIST="blacklist:admin:";

    public static final Long TOKEN_EXPIRES_TIME=24*3600*1000L;
}
