package com.atguigu.lease.web.admin.custom.interceptor;

import com.atguigu.lease.common.login.LoginUser;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * jwt拦截器，验证了请求的请求头的access-token中的token是否合法，用来判断是否是合法的用户。
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 前端登录后，后续请求都将JWT，放置于HTTP请求的Header中，其Header的key为`access-token`。
        String token = request.getHeader("access-token");

        // 解析该token，如果成功则放行，如果失败，则拦截。因为在parseToken中抛出异常，所以这里不需要显式拦截。
        Claims claims = JwtUtil.parseToken(token);

        Long userId = claims.get("userId", Long.class);//从token中解析出userId
        String username = claims.get("username", String.class);//从token中解析出username

        LoginUserHolder.setLoginUser(new LoginUser(userId, username));//将loginUser放入threadlocal中。

        // 放行。
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //释放线程资源。
        LoginUserHolder.clear();
    }
}
