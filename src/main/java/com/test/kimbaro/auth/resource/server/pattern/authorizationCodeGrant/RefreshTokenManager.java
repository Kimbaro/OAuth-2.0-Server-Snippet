package com.test.kimbaro.auth.resource.server.pattern.authorizationCodeGrant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import java.util.Base64;
import java.util.Date;

public class RefreshTokenManager {
    @Value("${jwt.private.key}")
    private String secretKey; //JWT 보안키
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 600; // Refresh Token 만료시간 10시간 = 600분

    /**
     * TODO : 여기에 Redis JPA Repo Instance 구성할것
     */

    public String createRefreshToken(String... claim) {
        Claims claims = Jwts.claims().setSubject(claim[0]); //사용자 PK 정보를 저장
        claims.put("exampleKey1", claim[1]);                //토큰 Payload 구성 인자
        claims.put("exampleKey2", claim[2]);                //토큰 Payload 구성 인자
        claims.put("exampleKey3", claim[3]);                //토큰 Payload 구성 인자
        claims.put("exampleKey4", claim[4]);                //토큰 Payload 구성 인자

        Date now = new Date();
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        /**
         * TODO: Redis 저장 로직 구성
         *
         *         // redis에 저장
         *         redisTemplate.opsForValue().set(
         *                 claim[0],
         *                 refreshToken,
         *                 REFRESH_TOKEN_EXPIRE_TIME,
         *                 TimeUnit.MILLISECONDS
         *         );
         *
         * */
        return refreshToken;
    }


    /**
     * TODO: 구성해야합니다
     * 1. getAuthentication() DB에 저장된 인증정보 조회
     * 2. resolveToken() 토큰 파라미터 파싱 Authorization 제거, Bearer 제거
     * 3. validateToken() 토큰 파라미터 검증
     * */
}
