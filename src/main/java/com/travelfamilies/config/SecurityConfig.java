package com.travelfamilies.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final CorsConfig corsConfig;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AccessDeniedHandler accessDeniedHandler,
                          AuthenticationEntryPoint authenticationEntryPoint, CorsConfig corsConfig) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.corsConfig = corsConfig;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfig corsConfig){

        http
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))

                // 1. 关闭 CSRF
                .csrf(csrf -> csrf.disable())

                // 2. 禁用 Session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 请求拦截规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/login", "/user", "/admin", "/admin/login", "/error", "/upload", "/admin/resetPassword", "/user/wx-login", "/user/wx-profile").permitAll()
                        .requestMatchers("/order/checkIn", "/order/checkOut", "/order/getOrderByGuest",
                                "/hotel/add", "/hotel/addRoom", "/hotel/modify", "/hotel/updateRoom",
                                "/hotel/updateDayMess", "/coupon/add", "/hotel/getOwner").hasRole("HOTEL")
                        .requestMatchers("/spot/addSpot", "/spot/update", "/spot/delete", "/coupon/add", "/hotel/list", "/hotel/all").hasRole("ADMIN")
                        .requestMatchers("/hotel/status/**").hasAnyRole("ADMIN", "HOTEL")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
