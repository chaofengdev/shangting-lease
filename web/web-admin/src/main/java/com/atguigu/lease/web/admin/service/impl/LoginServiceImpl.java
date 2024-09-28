package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.model.entity.SystemUser;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.admin.mapper.SystemUserMapper;
import com.atguigu.lease.web.admin.service.LoginService;
import com.atguigu.lease.web.admin.service.SystemUserService;
import com.atguigu.lease.web.admin.vo.login.CaptchaVo;
import com.atguigu.lease.web.admin.vo.login.LoginVo;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserInfoVo;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SystemUserMapper systemUserMapper;

    @Override
    public CaptchaVo getCaptcha() {
        //使用easyCaptcha生成验证码图片
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);//新建实例，配置相关信息
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);//设置字符类型

        //保存验证码uuid+code到redis中
        String code = specCaptcha.text().toLowerCase();//忽略大小写
        String key = RedisConstant.ADMIN_LOGIN_PREFIX + UUID.randomUUID();//根据规则拼接key
        redisTemplate.opsForValue().set(key, code, RedisConstant.ADMIN_LOGIN_CAPTCHA_TTL_SEC, TimeUnit.SECONDS);//存入redis中，带有过期时间。

        //构造vo并返回。
        String image = specCaptcha.toBase64();//对图片进行base64编码
        return new CaptchaVo(image, key);//返回image+key
    }

    @Override
    public String login(LoginVo loginVo) {
        //1.判断是否输入了验证码
        if (!StringUtils.hasText(loginVo.getCaptchaCode())) {
            throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_NOT_FOUND);
        }

        //2.校验验证码
        String code = redisTemplate.opsForValue().get(loginVo.getCaptchaKey());//从redis中取key对应的验证码code
        if (code == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_EXPIRED);//验证码过期（redis中没有key-code，说明已经过期。）
        }

        if (!code.equals(loginVo.getCaptchaCode().toLowerCase())) {
            throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_ERROR);//验证码错误
        }

        //3.校验用户是否存在
        SystemUser systemUser = systemUserMapper.selectOneByUsername(loginVo.getUsername());//需要自己实现，因为mybatis plus注解了password字段。

        if (systemUser == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);//账户不存在异常。
        }

        //4.校验用户是否被禁
        if (systemUser.getStatus() == BaseStatus.DISABLE) {
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_DISABLED_ERROR);//账户被封禁异常。
        }

        //5.校验用户密码
        if (!systemUser.getPassword().equals(DigestUtils.md5Hex(loginVo.getPassword()))) {//用户密码和前端数据加密后的密码不相同。
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);//密码错误异常。
        }

        //6.利用工具类JwtUtil创建并返回token
        return JwtUtil.createToken(systemUser.getId(), systemUser.getUsername());
    }

    @Override
    public SystemUserInfoVo getLoginUserInfoById(Long userId) {
        //根据userId查询用户
        SystemUser systemUser = systemUserMapper.selectById(userId);

        //构造返回对象systemUserInfoVo
        SystemUserInfoVo systemUserInfoVo = new SystemUserInfoVo();
        systemUserInfoVo.setName(systemUser.getName());
        systemUserInfoVo.setAvatarUrl(systemUser.getAvatarUrl());

        //返回
        return systemUserInfoVo;
    }
}
