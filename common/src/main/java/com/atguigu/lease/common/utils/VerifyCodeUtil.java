package com.atguigu.lease.common.utils;


import java.util.Random;

/**
 * 生成随机验证码的工具类。
 */
public class VerifyCodeUtil {

    /**
     * 生成指定长度的随机验证码。
     * @param length
     * @return
     */
    public static String getVerifyCode(int length) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            builder.append(random.nextInt(10));
        }

        return builder.toString();
    }
}
