package com.mpvaitheeswaran.ecommerce.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http.
                csrf(csrf->csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())   // 🔥 allow iframe
                )
                .authorizeHttpRequests(auth-> auth.anyRequest().permitAll())
                .build();
    }
}
