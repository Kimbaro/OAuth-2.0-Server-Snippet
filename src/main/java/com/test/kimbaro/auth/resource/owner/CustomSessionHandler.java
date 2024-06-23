package com.test.kimbaro.auth.resource.owner;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class CustomSessionHandler implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("CustomSessionHandler preHandle 인입확인");
        // 사용자의 모든 요청에 대해 동작하기 때문에 작성에 주의	// static 요청에도 동작함
        // 로그출력 남기지 말 것! 작업후 반드시 삭제
        // 명확한 조건에 따라 작성 할 것!

//		if(Objects.isNull(request.getSession().getAttribute("loginUser"))) {
//			String uri = ""+request.getRequestURI();
//
//		}
        return true;
    }

}
