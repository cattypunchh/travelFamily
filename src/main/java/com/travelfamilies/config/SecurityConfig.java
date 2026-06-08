package com.travelfamilies.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                        .requestMatchers("/user/login", "/user", "/admin", "/admin/login", "/error",
                                "/admin/resetPassword", "/user/wx-login", "/user/wx-profile").permitAll()
                        // 酒店管理员专属
                        .requestMatchers(HttpMethod.POST, "/hotel").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.POST, "/hotel/room").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.PUT, "/hotel/{id}").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.PUT, "/hotel/room").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.PUT, "/hotel/dayMess").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.POST, "/hotel/getOwner").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.PUT, "/coupon/{couponId}").hasRole("HOTEL")
                        .requestMatchers("/order/checkIn", "/order/checkOut", "/order/getOrderByGuest").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.DELETE, "/hotel/{id}").hasRole("HOTEL")
                        .requestMatchers(HttpMethod.DELETE, "/hotel/room/{id}").hasRole("HOTEL")
                        // 系统管理员专属
                        .requestMatchers(HttpMethod.PUT, "/admin/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/admin/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/admin/query").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/spot").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/spot/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/spot/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/hotel/list").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/hotel/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/hotel/status/{id}").hasRole("ADMIN")
                        // 管理员+酒店管理员
                        .requestMatchers("/hotel/status/**").hasAnyRole("ADMIN", "HOTEL")
                        .requestMatchers(HttpMethod.POST, "/coupon").hasAnyRole("ADMIN", "HOTEL")
                        .requestMatchers(HttpMethod.PUT, "/coupon/{couponId}").hasAnyRole("ADMIN", "HOTEL")

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
