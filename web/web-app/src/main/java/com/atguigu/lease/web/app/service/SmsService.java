package com.atguigu.lease.web.app.service;

public interface SmsService {
    void sendCode(String phone, String verifyCode);
}
