package com.example.api.interceptor;

import com.example.api.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var req = RequestUtil.getHttpServletRequest();
        if (req == null) return true;

        String xForwardedFor = req.getHeader("x-forwarded-for");
        String bizUserAgent = req.getHeader("User-Agent");
        String xAssignedIp = req.getHeader("x-assigned-ip");


        log.info("xForwardedFor :{}, bizUserAgent:{}, xAssignedIp:{}", xForwardedFor, bizUserAgent, xAssignedIp);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        MDC.remove("xForwardedFor");
        MDC.remove("bizUserAgent");
        MDC.remove("xAssignedIp");
    }
}
