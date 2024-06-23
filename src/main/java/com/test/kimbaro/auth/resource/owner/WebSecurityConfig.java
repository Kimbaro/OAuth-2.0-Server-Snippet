package com.test.kimbaro.auth.resource.owner;

import com.test.kimbaro.auth.resource.server.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {


    private final JwtTokenProvider jwtTokenProvider;
    private static final String[] STATIC_RESOURCE_LOCATION = {"/**/*", "/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/swagger-ui/**"};
    private static final String[] NON_AUTHENTICATED_LOCATION = {"/api/v1/**", "/swagger-ui/**"};


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrfConfigurer -> {
                    // token 사용방식이기 때문에 csrf를 disable 합니다.
                    csrfConfigurer.disable();
                })
                .headers(headersConfigurer -> {
                    headersConfigurer.addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy", "default-src 'self'"));
                    headersConfigurer.addHeaderWriter(new StaticHeadersWriter("X-WebKit-CSP", "default-src 'self'"));
                    headersConfigurer.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable());
                })
                .sessionManagement(sessionManagementConfigurer -> {
                    // 세션을 사용하지 않게 설정
                    sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authorizeHttpRequests(authorizeHttpRequests -> {
                    authorizeHttpRequests.requestMatchers("/api/account/**").permitAll()
                            .requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers("/v3/api-docs/**", "swagger*/**").permitAll() // *중요
                            .requestMatchers("/api/docs/**").permitAll() // * 중요
                            .requestMatchers("/api/participant/**").permitAll()
                            .requestMatchers("/api/demandLocation/**").permitAll()
                            .requestMatchers("/api/auditHistory/**").permitAll()
                            .requestMatchers("/api/summary/**").permitAll()
                            .requestMatchers("/api/summary/**").permitAll()
                            .requestMatchers("/api/auth/**").permitAll()
                            .anyRequest().authenticated();  //위 모든 항목에 대한 요청에 검증을 요구.
                })
                .formLogin(formLoginConfigurer -> formLoginConfigurer.disable())
                .logout(logoutConfigurer -> logoutConfigurer.disable())
                .apply()
                .build();
        return http.build();
    }
}
