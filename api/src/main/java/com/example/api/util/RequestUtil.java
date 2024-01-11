package com.example.api.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestUtil {

    public static HttpServletRequest getHttpServletRequest() {
        var requestAttributes = RequestContextHolder.getRequestAttributes();
        if(requestAttributes == null) return null;

        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }
}
