package com.test.kimbaro.auth.resource.server.pattern.authorizationCodeGrant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import java.util.Base64;
import java.util.Date;

public class RefreshTokenManager {
    @Value("${jwt.private.key}")
    private String secretKey; //JWT ����Ű
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 600; // Refresh Token ����ð� 10�ð� = 600��

    /**
     * TODO : ���⿡ Redis JPA Repo Instance �����Ұ�
     */

    public String createRefreshToken(String... claim) {
        Claims claims = Jwts.claims().setSubject(claim[0]); //����� PK ������ ����
        claims.put("exampleKey1", claim[1]);                //��ū Payload ���� ����
        claims.put("exampleKey2", claim[2]);                //��ū Payload ���� ����
        claims.put("exampleKey3", claim[3]);                //��ū Payload ���� ����
        claims.put("exampleKey4", claim[4]);                //��ū Payload ���� ����

        Date now = new Date();
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        /**
         * TODO: Redis ���� ���� ����
         *
         *         // redis�� ����
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
     * TODO: �����ؾ��մϴ�
     * 1. getAuthentication() DB�� ����� �������� ��ȸ
     * 2. resolveToken() ��ū �Ķ���� �Ľ� Authorization ����, Bearer ����
     * 3. validateToken() ��ū �Ķ���� ����
     * */
}
