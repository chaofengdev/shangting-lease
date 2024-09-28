package com.atguigu.lease.web.app.service.impl;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
//import com.aliyun.teaopenapi.Client;
import com.aliyun.dysmsapi20170525.Client;
import com.atguigu.lease.web.app.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private Client client;

    /**
     * 发送短信功能。
     * 提供手机号码和具体的验证码，通过阿里云的短信服务，发送短信到用户手机。
     * @param phone
     * @param code
     */
    @Override
    public void sendCode(String phone, String code) {
        //因为是使用的阿里云短信的免费服务，所以格式受限，只能按照固定格式。原则上是用来测试的。
        SendSmsRequest smsRequest = new SendSmsRequest();
        smsRequest.setPhoneNumbers(phone);
        smsRequest.setSignName("阿里云短信测试");//对应签名，就是短信前面的公司名
        smsRequest.setTemplateCode("SMS_154950909");//模板，阿里云写好的短信的模板
        smsRequest.setTemplateParam("{\"code\":\"" + code + "\"}");//填充模板里的参数，这里对code进行赋值。

        try {
            //如果提示没有sendSms方法，务必确认导入的Client包是否正确。
            client.sendSms(smsRequest);//阿里云提供的api，表示发送短信。
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
