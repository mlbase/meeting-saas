package com.meeting.demo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {
    
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    // Allow access to H2 console
                    .requestMatchers("/h2-console/**").permitAll()
                    
                    // Allow access to signup related endpoints
                    .requestMatchers("/page/signup", "/page/login").permitAll()
                    .requestMatchers("/users").permitAll()
                    .requestMatchers("/users/**").permitAll()
                    
                    // Allow access to static resources
                    .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                    
                    // Allow access to templates and resources
                    .requestMatchers("/templates/**", "/resources/**").permitAll()
                    
                    // Allow access to actuator endpoints if needed
                    .requestMatchers("/actuator/**").permitAll()
                    
                    // Require authentication for all other requests
                    .anyRequest().authenticated()
            }
            .csrf { csrf -> 
                csrf
                    // Disable CSRF for API endpoints and H2 console
                    .ignoringRequestMatchers("/users/**")
                    .ignoringRequestMatchers("/api/**")
                    .ignoringRequestMatchers("/h2-console/**")
            }
            .headers { headers ->
                headers.frameOptions().disable() // Allow H2 console frames
            }
            .formLogin { form ->
                form
                    .loginPage("/page/login")
                    .defaultSuccessUrl("/page/dashboard", true)
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/page/login")
                    .permitAll()
            }
        
        return http.build()
    }
}