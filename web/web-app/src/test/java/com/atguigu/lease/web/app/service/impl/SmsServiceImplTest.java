package com.atguigu.lease.web.app.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SmsServiceImplTest {


    @Autowired
    private SmsServiceImpl smsService;


    /**
     * 测试验证码发送功能是否正常。
     * 运行，查看手机是否收到验证码。
     */
    @Test
    void sendCode() {
        smsService.sendCode("15722922862", "4567");
    }
}