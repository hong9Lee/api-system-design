package com.example.api.interceptor;

import com.example.api.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var httpServletRequest = RequestUtil.getHttpServletRequest();
        if (httpServletRequest == null) return true;

        // TODO: forwarded, agent, ip 등 추가 필요
        return true;

    }
}
