package com.test.kimbaro.auth.resource.server;

import com.test.kimbaro.auth.resource.server.pattern.authorizationCodeGrant.AccessTokenManager;
import com.test.kimbaro.auth.resource.server.pattern.authorizationCodeGrant.RefreshTokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

//해당 클래스는 JwtTokenProvider가 검증을 끝낸 Jwt로부터 유저 정보를 조회해와서 UserPasswordAuthenticationFilter 로 전달합니다.
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final AccessTokenManager accessTokenManager;
    private final RefreshTokenManager refreshTokenManager;

    private static final String ATK_RESOLVE_TYPE = "accessToken";

    private static final String RTK_RESOLVE_TYPE = "refreshToken";


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        System.out.println("[doFilter]  호출");

        // 헤더에서 JWT 를 받아옵니다.
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request, ATK_RESOLVE_TYPE);
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();

        System.out.println("[doFilter] 요청 URI: " + requestURI);
        System.out.println("[doFilter] AccessToken: " + httpServletRequest.getHeader("Authorization"));
        System.out.println("[doFilter] RefreshToken: " + httpServletRequest.getHeader("RefreshToken"));

        // 유효한 토큰인지 확인합니다.
        if (token != null && jwtTokenProvider.validateToken(token)) {

            System.out.println("*** [doFilter] Access Token 유효");

            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            System.out.println("authentication: " + authentication);

            // SecurityContext 에 Authentication 객체를 저장합니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security context에 '{}' 인증 정보를 저장했습니다, url: {}", authentication.getName(), requestURI);
        } else {
            System.out.println("*** [doFilter] Access Token 만료");

        }
        chain.doFilter(request, response);
    }


}
