package com.test.kimbaro.auth.resource.server.pattern.authorizationCodeGrant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AccessTokenManager {

    @Value("${jwt.private.key}")
    private String secretKey; //JWT ����Ű
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 180; // Access Token ����ð� 3�ð� = 180��

    /**
     * TODO : ���⿡ Redis JPA Repo �����Ұ�
     */

    public String createAccessToken(String... claim) {
        Claims claims = Jwts.claims().setSubject(claim[0]); //����� PK ������ ����
        claims.put("exampleKey1", claim[1]);                //��ū Payload ���� ����
        claims.put("exampleKey2", claim[2]);                //��ū Payload ���� ����
        claims.put("exampleKey3", claim[3]);                //��ū Payload ���� ����
        claims.put("exampleKey4", claim[4]);                //��ū Payload ���� ����

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }


    /**
     * TODO: �����ؾ��մϴ�
     * 1. getAuthentication() DB�� ����� �������� ��ȸ
     * 2. resolveToken() ��ū �Ķ���� �Ľ� Authorization ����, Bearer ����
     * 3. validateToken() ��ū �Ķ���� ����
     * */


}
