package com.test.kimbaro.auth.resource.server;

import com.bc.app.auth.UserDetailServiceImpl;
import com.bc.app.jpa.db1.entity.Authorities;
import com.bc.app.jpa.db1.entity.User;
import com.bc.app.jpa.db1.repository.AuthRepository;
import com.bc.app.jpa.db1.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 토큰을 생성하고 검증하는 클래스
 * 해당 컴포넌트는 필터클래스에서 사전 검증을 칩니다.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.private.key}")
    private String secretKey; //JWT 보안키
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 180; // Access Token 만료시간 3시간 = 180분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 600; // Refresh Token 만료시간 10시간 = 600분

    private static final String ATK_RESOLVE_TYPE = "accessToken";
    private static final String RTK_RESOLVE_TYPE = "refreshToken";

    // Repository
    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    private final UserDetailServiceImpl userDetailServiceImpl;

    // 객체 초기화 secretKey를 Base64로 인코딩 한다.
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * Access Token 생성
     * DB에 저장하지 않으며 로그인 성공시 Response로 전달 한다.
     * 토큰 탈취시 보안 취약 시간을 최소화 하기위해 만료시간을 짧게 설정한다.
     */
    public String createToken(String userPk, String role) {
//    public String createToken (String userPk, String serviceId, String role) {

        System.out.println("[createToken] 호출");
        System.out.println("[createToken] params userPk: " + userPk + " role: " + role);

        Claims claims = Jwts.claims().setSubject(userPk); //JWT payload에 저장되는 정보단위, 보통 여기서 user 식별 값을 넣는다.
//        claims.put("serviceId", serviceId); // 정보는 key / value 쌍으로 저장된다.
        claims.put("roles", role);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME)) // set Expire Time
                .signWith(SignatureAlgorithm.RS256, secretKey) // 사용할 암호화 알고리즘과 signature 에 들어갈 secret 세팅
                .compact();
    }

    /**
     * Refresh Token 생성
     * DB에 저장하며 Access Token 발급시 사용된다.
     * 상대적으로 만료기간이 긴 RTK는 탈취시 보안에 취약함으로 claim을 최소화 한다.
     *
     * @param userPk: userEmail
     * @return
     */
    //JWT 리프레시 토큰 생성
    public String createRefreshToken(String userPk) {


        System.out.println("[createRefreshToken] 호출");
        System.out.println("[createRefreshToken] params userPk: " + userPk);
        Claims claims = Jwts.claims().setSubject(userPk);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 사용할 암호화 알고리즘과 signature 에 들어갈 secret값 세팅
                .compact();
    }

    /**
     * 토큰에서 인증 정보를 조회 한다.
     */
    public Authentication getAuthentication(String token) {

        System.out.println("[getAuthentication] 호출");

        token = BearerRemove(token); // JWT 토큰 접두사 bearer 제거
        UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(this.getUserPk(token)); // getUserPk= userEmail
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * token 에서 사용자 PK(userName) 추출
     */
    public String getUserPk(String token) {
        System.out.println("[getUserPk] 호출");
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject(); //AT subject: userPk(userId)

        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 토큰에서 회원 정보 추출
     */
    public User findUserInfoByRequest(HttpServletRequest request, String resolveType) {
        System.out.println("[findUserInfoByRequest] 호출");
        String token = resolveToken(request, resolveType);
        String userPk = getUserPk(token);

        if (userPk == null) {
            System.out.println("[findUserInfoByRequest] userPk = null");
            return null;
        }

        System.out.println(">>>> userPk: " + userPk);
//        return userRepository.findByUserEmail(userPk);
        return userRepository.findByUserId(userPk);
    }

    /**
     * 헤더에서 token 추출
     * Request의 Header에서 token 값을 가져온니다. "Authorization" : "Token 값"
     */
    public String resolveToken(HttpServletRequest request, String resolveType) {

        System.out.println("[resolveToken] 호출");

        if (request.getHeader("Authorization") != null && resolveType.equals(ATK_RESOLVE_TYPE)) {
            String accessToken = BearerRemove(request.getHeader("Authorization")); // Bearer 제거
            return accessToken;
        }
        if (request.getHeader("RefreshToken") != null && resolveType.equals(RTK_RESOLVE_TYPE)) {
            String refreshToken = BearerRemove(request.getHeader("RefreshToken")); // Bearer 제거
            return refreshToken;
        }
        return null;
    }

    /**
     * Token 만료 검사
     */
    public boolean validateToken(String jwtToken) {

        System.out.println("[validateToken] 진입");

        jwtToken = BearerRemove(jwtToken); // Bearer 제거

        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
//            System.out.println("claims: " + claims);
//            System.out.println("토큰 만료 시간:" + claims.getBody().getExpiration());
//            System.out.println("토큰 만료 여부:" + claims.getBody().getExpiration().before(new Date()));
            return !claims.getBody().getExpiration().before(new Date()); //토큰 만료 시간이 현재시간보다 미래이면 return true
        } catch (Exception e) {
            System.out.println("[validateToken] 토큰 만료");
            return false;
        }
    }

    /**
     * Token 위변조 검사
     */
    public boolean validateToken2(String jwtToken) {

        System.out.println("token 위변조 검사");

        jwtToken = BearerRemove(jwtToken); // Bearer 제거

        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            if (claims.getSignature() != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("toekn 유효성 검사 실패");
            return false;
        }
    }

    /**
     * 토큰 앞부분 ('Bearer') 제거 메소드
     */
    private String BearerRemove(String token) {
        System.out.println("[BearerRemove] 호출");
        System.out.println("[BearerRemove] token: " + token);
        return token.replace("Bearer ", "");
    }


    /**
     * 토큰 리프레쉬 API
     *
     * @param request
     * @return
     */
    public Map<String, String> tokenRefresh(HttpServletRequest request) {

        System.out.println("[tokenRefresh] 호출");

        // 1. request 로 부터 RTK, serviceId 추출
//        String userRefershToken = jwtTokenProvider.resolveToken(request);
        String userRefreshToken = request.getHeader("RefreshToken");
        String useServiceId = request.getHeader("ServiceId"); // request로부터 serviceId 추출
        log.info("[tokenRefresh] userRefreshToken: {}", userRefreshToken);
        log.info("[tokenRefresh] useServiceId: {}", useServiceId);

        Map<String, String> resultMap = new HashMap<>();

        // 2. RTK 유효성 검사.
        if (!validateToken(userRefreshToken)) {
            System.out.println("[tokenRefresh] refreshToken 만료");
            resultMap.put("response", "refreshToken 만료");
            return resultMap;
        }
        // 3. 전달받은 토큰으로 user정보 조회
        User user = findUserInfoByRequest(request, RTK_RESOLVE_TYPE);
        log.info("[tokenRefresh] user: {} ", user.toString());

        // 4. DB에 저장된 RKT 조회
//        Authorities authorities = authRepository.findByUidAndServiceId(user.getUid(), useServiceId);
        Authorities authorities = authRepository.findByUserId(user.getUserId());

        if (authorities == null) {
            log.info(">>>>> RTK 찾을 수 없음");
            resultMap.put("response", "저장된 토큰을 찾을 수 없음");
            return resultMap;
        }

        // 5. 전달받은 RTK와 저장된 RKT 비교
        if (!authorities.getRefreshToken().equals(userRefreshToken)) {
            log.info(">>>>> RTK 불일치");
            resultMap.put("response", "refreshToken 불일치");
            return resultMap;
        }

        // 6. ATK 와 RTK 재발급
//        String newAccessToken = createToken(user.getUserEmail(), useServiceId, user.getRole());
//        String newRefreshToken = createRefreshToken(user.getUserEmail());
        String newAccessToken = createToken(user.getUserId(), user.getUserType());
        String newRefreshToken = createRefreshToken(user.getUserId());

        // 현재 날짜 생성
        String nowDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // 7. 재발급 받은 RTK 로 교체
        authRepository.modifyRefreshToken(user.getUserId(), newRefreshToken);
//        authRepository.modifyRefreshToken(user.getUid(), useServiceId, newRefreshToken, nowDateTime);

        // 8. resultMap 빌드

        resultMap.put("response", "Token Refresh 성공");
        resultMap.put("newAccessToken", newAccessToken);
        resultMap.put("newRefreshToken", newRefreshToken);

        return resultMap;

    }


}
