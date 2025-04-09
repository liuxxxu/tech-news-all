package com.liuxu.common.interceptor;

import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;


public class UserIdInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取请求头中的用户信息
        String userId = request.getHeader("userId");
        // 2.判断是否为空
        if (StringUtils.hasText(userId)) {
            // 不为空，保存到ThreadLocal
            AppThreadLocalUtils.setUserId(Long.valueOf(userId));
        }
        // 3.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        AppThreadLocalUtils.clear();
    }
}