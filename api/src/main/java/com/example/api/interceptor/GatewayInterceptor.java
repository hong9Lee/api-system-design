package com.example.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Gateway를 통한 요청만 허용한다.
 * 요청 Header에 X-Gateway-Auth 검사.

 추가적으로 아래 방법으로도 구현 가능할 것 같다.
 - IP 화이트 리스팅
 - 로드 밸런서를 통해 Api 요청을 Gateway로 전부 라우팅
 - Spring Cloud Gateway의 라우팅 기능 사용해보기.
 */
public class GatewayInterceptor implements HandlerInterceptor {

    private static final String GATEWAY_AUTH_HEADER = "X-Gateway-Auth";
    private static final String SECRET_KEY = "967869b7-56b8-4766-8473-7baa04a499ab";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String headerValue = request.getHeader(GATEWAY_AUTH_HEADER);

        if (SECRET_KEY.equals(headerValue)) return true;
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        return false;
    }
}
