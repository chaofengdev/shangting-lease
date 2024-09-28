package com.atguigu.lease.common.constant;

/**
 * 为方便管理，可以将Reids相关的一些值定义为常量，例如key的前缀、TTL时长，内容如下。
 */
public class RedisConstant {
    //web-admin模块
    public static final String ADMIN_LOGIN_PREFIX = "admin:login:";
    public static final Integer ADMIN_LOGIN_CAPTCHA_TTL_SEC = 60;

    //web-app模块
    public static final String APP_LOGIN_PREFIX = "app:login:";
    public static final Integer APP_LOGIN_CODE_RESEND_TIME_SEC = 60;
    public static final Integer APP_LOGIN_CODE_TTL_SEC = 60 * 10;
    public static final String APP_ROOM_PREFIX = "app:room:";
}
