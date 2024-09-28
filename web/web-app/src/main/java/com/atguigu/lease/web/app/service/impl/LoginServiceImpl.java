package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.common.utils.VerifyCodeUtil;
import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.app.service.LoginService;
import com.atguigu.lease.web.app.service.SmsService;
import com.atguigu.lease.web.app.service.UserInfoService;
import com.atguigu.lease.web.app.vo.user.LoginVo;
import com.atguigu.lease.web.app.vo.user.UserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {


    @Autowired
    //private RedisTemplate redisTemplate;//为了避免客户端序列化异常，可以使用 StringRedisTemplate.
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserInfoService userInfoService;



    /**
     * 生成验证码，并发送短信，最后将验证码存入redis。
     * @param phone
     */
    @Override
    public void getSMSCode(String phone) {
        //1. 检查手机号码是否为空
        if (!StringUtils.hasText(phone)) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_PHONE_EMPTY);//手机号码为空异常。
        }

        //2. 检查Redis中是否已经存在该手机号码的key
        String key = RedisConstant.APP_LOGIN_PREFIX + phone;
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            //若存在，则检查其存在的时间
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);//注意这里是剩余存活时间，不是存在的时间。
            if (RedisConstant.APP_LOGIN_CODE_TTL_SEC - expire < RedisConstant.APP_LOGIN_CODE_RESEND_TIME_SEC) {
                //若存在时间不足一分钟，响应发送过于频繁 规定1分钟内只能请求一次验证码，防止被盗刷。
                //这里解释一下，redis存验证码10分钟，如果验证码剩余存活时间是9分30秒，那么抛异常，因为30秒之前就请求了这个验证码，间隔时间太短了。
                throw new LeaseException(ResultCodeEnum.APP_SEND_SMS_TOO_OFTEN);//验证码发送过于频繁异常。
            }
        }

        //3.发送短信，并将验证码存入Redis
        String verifyCode = VerifyCodeUtil.getVerifyCode(6);//调用工具类生成验证码。长度为6.
        smsService.sendCode(phone, verifyCode);//调用smsService向指定phone发送验证码code。
        redisTemplate.opsForValue().set(key, verifyCode, RedisConstant.APP_LOGIN_CODE_TTL_SEC, TimeUnit.SECONDS);//将手机号（拼接key）+验证码存入redis。
    }


    /**
     * 判断用户是否存在，不存在则注册，存在则登录返回jwt-token
     * @param loginVo
     * @return
     */
    @Override
    public String login(LoginVo loginVo) {
        //1.判断手机号码和验证码是否为空
        if (!StringUtils.hasText(loginVo.getPhone())) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_PHONE_EMPTY);//号码为空异常。
        }

        if (!StringUtils.hasText(loginVo.getCode())) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EMPTY);//验证码为空异常。
        }

        //2.校验验证码
        String key = RedisConstant.APP_LOGIN_PREFIX + loginVo.getPhone();//拼接key
        String code = redisTemplate.opsForValue().get(key);//获取value（验证码）
        if (code == null) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EXPIRED);//验证码过期异常
        }

        if (!code.equals(loginVo.getCode())) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_ERROR);//验证码错误异常。
        }

        //3.判断用户是否存在,不存在则注册（创建用户）
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getPhone, loginVo.getPhone());
        UserInfo userInfo = userInfoService.getOne(queryWrapper);//根据手机号查询用户，我们这里的设计是手机号为账号名
        if (userInfo == null) {//没有这个用户，则注册用户。
            userInfo = new UserInfo();
            userInfo.setPhone(loginVo.getPhone());
            userInfo.setStatus(BaseStatus.ENABLE);
            userInfo.setNickname("用户-"+loginVo.getPhone().substring(6));
            userInfoService.save(userInfo);//保存用户，自动回显id
        }

        //4.判断用户是否被禁
        if (userInfo.getStatus().equals(BaseStatus.DISABLE)) {
            throw new LeaseException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);//用户被禁异常。
        }

        //5.创建并返回TOKEN
        return JwtUtil.createToken(userInfo.getId(), loginVo.getPhone());//调用工具类。
    }


    /**
     * 根据userId，获取用户信息。
     * @param userId
     * @return
     */
    @Override
    public UserInfoVo getUserInfoById(Long userId) {
        UserInfo userInfo = userInfoService.getById(userId);
        return new UserInfoVo(userInfo.getNickname(), userInfo.getAvatarUrl());//拼接vo对象。
    }
}
