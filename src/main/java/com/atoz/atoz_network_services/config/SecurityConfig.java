package com.atoz.atoz_network_services.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            	    .requestMatchers(
            	        HttpMethod.POST,
            	        "/api/enquiries",
            	        "/api/complaints",
            	        "/api/service-requests"
            	    ).permitAll()

            	    .requestMatchers(
            	        "/api/enquiries/**",
            	        "/api/customers/**",
            	        "/api/plans/**",
            	        "/api/service-requests/**",
            	        "/api/complaints/**"
            	    ).authenticated()

            	    .anyRequest().permitAll()
            	)
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
