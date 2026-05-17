package com.travelfamilies.tools;

public final class RedisConstant {

    /*登录用户的token*/
    public static final String USER_TOKEN = "login:user:token:";
    /*登录系统管理员的token*/
    public static final String ADMIN_TOKEN = "login:admin:token:";
    /*登录酒店管理员的token*/
    public static final String HOTEL_ADMIN_TOKEN = "login:hotelAdmin:token:";
    /*处于黑名单的用户*/
    public static final String USER_BLACK_LIST = "blacklist:user:";
    /*处于黑名单的系统管理员*/
    public static final String ADMIN_BLACK_LIST = "blacklist:admin:";
    /*处于黑名单的酒店管理员*/
    public static final String HOTEL_ADMIN_BLACK_LIST = "blacklist:hotelAdmin:";
    /*热点spot 前十名*/
    public static final String SPOT_TOP10_LIST = "spot";
    /*spot详情*/
    public static final String SPOT_TYPE_DETAIL = "spot:type:detail";
    /*某个文章的浏览量*/
    public static final String SPOT_VIEWS = "spot:views:";
    /*防止某个用户恶意刷某景点浏览量*/
    public static final String SPOT_VIEWS_USER = "spot:views:user:";
    /*景点评分及评论总数*/
    public static final String COMMENT_SPOT_SCORE = "comment:spot:score:";
    public static final String COMMENT_SPOT_COUNT = "comment:spot:count:";
    /*景点评分及评论总数*/
    public static final String COMMENT_HOTEL_SCORER = "comment:hotel:score:";
    public static final String COMMENT_HOTEL_COUNT = "comment:hotel:count:";

    /*酒店某房型价格*/
    public static final String HOTEL_ROOM_PRICE = "hotel:price:";
    /*酒店某房型库存*/
    public static final String HOTEL_ROOM_STOCK = "hotel:stock:";
    /*优惠券库存*/
    public static final String COUPON_STOCK = "coupon:stock:";
    /*优惠券每人限制领取张数*/
    public static final String COUPON_LIMIT = "coupon:limit:";
    /*优惠券优惠力度*/
    public static final String COUPON_DISCOUNT = "coupon:discount:";
    /*优惠券某用户领取某优惠券情况说明*/
    public static final String COUPON_GET_DETAIL = "coupon:get:detail:";

    /*某日某酒店实时房型情况*/
    public static final String HOTEL_ROOM_DYNAMIC = "hotel:room:dynamic:";
    public static final Long HOTEL_ROOM_DYNAMIC_EXPIRES = 1L;

    public static final Long SPOT_VIEWS_USER_EXPIRES = 5 * 1000L;
    public static final Long TOKEN_EXPIRES_TIME = 24 * 3600 * 1000L;
}
