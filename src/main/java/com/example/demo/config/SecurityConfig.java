////////package com.example.demo.config;
////////import org.springframework.context.annotation.Bean;
////////import org.springframework.context.annotation.Configuration;
////////import org.springframework.security.authentication.AuthenticationManager;
////////import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
////////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////////import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
////////import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
////////import org.springframework.security.core.userdetails.UserDetailsService;
////////import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
////////import org.springframework.security.crypto.password.PasswordEncoder;
////////import org.springframework.security.provisioning.InMemoryUserDetailsManager;
////////import org.springframework.security.web.SecurityFilterChain;
////////import org.springframework.web.cors.CorsConfigurationSource;
////////import org.springframework.web.cors.CorsConfiguration;
////////import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
////////import org.springframework.security.config.Customizer;
////////
////////import java.util.List;
////////
////////@Configuration
////////@EnableWebSecurity
////////public class SecurityConfig {
////////
////////    @Bean
////////    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////////        http
////////            .cors(Customizer.withDefaults()) // <-- Enable CORS support
////////            .csrf(AbstractHttpConfigurer::disable)
////////            .authorizeHttpRequests(auth -> auth
////////                .requestMatchers(
////////                    "/login", "/register", "/auction/**",
////////                    "/api/**", "/api/auth/register", "/api/auth/login",
////////                    "/admin/login", "/admin/**", "/user/**", "/bids/**", "/documents/**","/api/test/sendEmail"
////////                ).permitAll()
////////                .anyRequest().authenticated()
////////            )
////////            .formLogin(AbstractHttpConfigurer::disable)
////////            .httpBasic(AbstractHttpConfigurer::disable);
////////
////////        return http.build();
////////    }
////////
////////    @Bean
////////    public AuthenticationManager authenticationManager(HttpSecurity http,
////////                                                       PasswordEncoder passwordEncoder,
////////                                                       UserDetailsService userDetailsService) throws Exception {
////////        return http
////////            .getSharedObject(AuthenticationManagerBuilder.class)
////////            .userDetailsService(userDetailsService)
////////            .passwordEncoder(passwordEncoder)
////////            .and()
////////            .build();
////////    }
////////
////////    @Bean
////////    public UserDetailsService userDetailsService() {
////////        return new InMemoryUserDetailsManager();
////////    }
////////
////////    @Bean
////////    public PasswordEncoder passwordEncoder() {
////////        return new BCryptPasswordEncoder();
////////    }
////////}
//////
//////
//////
//////
//////
//////
//////
//////
//////
//////
//////
//////
//////
//////
//////
//////package com.example.demo.config;
//////
//////import org.springframework.context.annotation.Bean;
//////import org.springframework.context.annotation.Configuration;
//////import org.springframework.security.authentication.AuthenticationManager;
//////import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//////import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//////import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//////import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//////import org.springframework.security.crypto.password.PasswordEncoder;
//////import org.springframework.security.web.SecurityFilterChain;
//////import org.springframework.web.cors.CorsConfiguration;
//////import org.springframework.web.cors.CorsConfigurationSource;
//////import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//////import org.springframework.security.config.Customizer;
//////
//////import java.util.List;
//////
//////@Configuration
//////@EnableWebSecurity
//////public class SecurityConfig {
//////
//////    @Bean
//////    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//////        http
//////            .cors(Customizer.withDefaults()) // Enables CORS
//////            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for APIs
//////            .authorizeHttpRequests(auth -> auth
//////                // These are the only public endpoints
//////                .requestMatchers("/api/auth/**").permitAll() 
//////                // All other requests must be authenticated
//////                .anyRequest().authenticated()
//////            );
//////
//////        return http.build();
//////    }
//////    
//////    // This bean is no longer needed with modern Spring Security if you have a UserDetailsService
//////    // Spring Boot will wire it automatically with the DaoAuthenticationProvider.
//////
//////    // Modern way to expose the AuthenticationManager bean
//////    @Bean
//////    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//////        return config.getAuthenticationManager();
//////    }
//////    
//////    // THE CONFLICTING BEAN HAS BEEN DELETED.
//////    // Spring will now find your CustomUserDetailsService automatically.
//////
//////    @Bean
//////    public PasswordEncoder passwordEncoder() {
//////        return new BCryptPasswordEncoder();
//////    }
//////    
//////    // Optional: A basic CORS configuration if you need it.
//////    @Bean
//////    CorsConfigurationSource corsConfigurationSource() {
//////        CorsConfiguration configuration = new CorsConfiguration();
//////        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // Your React app's URL
//////        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//////        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
//////        configuration.setAllowCredentials(true);
//////        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//////        source.registerCorsConfiguration("/**", configuration);
//////        return source;
//////    }
//////}
////
////
////
////
////
////
////
////
////
////
////
////
////package com.example.demo.config;
////
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.security.authentication.AuthenticationManager;
////import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
////import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
////import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
////import org.springframework.security.crypto.password.PasswordEncoder;
////import org.springframework.security.web.SecurityFilterChain;
////import org.springframework.web.cors.CorsConfiguration;
////import org.springframework.web.cors.CorsConfigurationSource;
////import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
////import org.springframework.security.config.Customizer;
////
////import java.util.List;
////
////@Configuration
////@EnableWebSecurity
////public class SecurityConfig {
////
////    @Bean
////    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////        http
////            .cors(Customizer.withDefaults()) // Enables CORS
////            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for APIs
////            .authorizeHttpRequests(auth -> auth
////                // Endpoints that do not require authentication
////                .requestMatchers("/api/auth/**", "/documents/**").permitAll() // <-- UPDATED: Added /documents/** here
////                // All other requests must be authenticated
////                .anyRequest().authenticated()
////            );
////
////        return http.build();
////    }
////
////    // Modern way to expose the AuthenticationManager bean
////    @Bean
////    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
////        return config.getAuthenticationManager();
////    }
////
////    @Bean
////    public PasswordEncoder passwordEncoder() {
////        return new BCryptPasswordEncoder();
////    }
////    
////    // A basic CORS configuration. Make sure the origin URL is correct.
////    @Bean
////    CorsConfigurationSource corsConfigurationSource() {
////        CorsConfiguration configuration = new CorsConfiguration();
////        // Make sure this matches your React app's URL (e.g., http://localhost:5173 or http://localhost:3000)
////        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000")); 
////        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
////        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "x-auth-token"));
////        configuration.setExposedHeaders(List.of("x-auth-token"));
////        configuration.setAllowCredentials(true);
////        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
////        source.registerCorsConfiguration("/**", configuration);
////        return source;
////    }
////}
////
//
//
//
//
//
//
//package com.example.demo.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.security.config.Customizer;
//
//import java.util.List;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .cors(Customizer.withDefaults()) // Enables CORS
//            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for APIs
//            .authorizeHttpRequests(auth -> auth
//                // Add "/admin/**" to the list of public endpoints
//                .requestMatchers("/api/auth/**", "/documents/**", "/admin/**").permitAll() 
//                // All other requests must be authenticated
//                .anyRequest().authenticated()
//            );
//
//        return http.build();
//    }
//
//    // Modern way to expose the AuthenticationManager bean
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//    
//    // A basic CORS configuration. Make sure the origin URL is correct.
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        // Make sure this matches your React app's URL (e.g., http://localhost:5173 or http://localhost:3000)
//        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000")); 
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "x-auth-token"));
//        configuration.setExposedHeaders(List.of("x-auth-token"));
//        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}
//




package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.Customizer;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) // Enables CORS
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for APIs
            .authorizeHttpRequests(auth -> auth
                // Add "/user/**" to the list of public endpoints
                .requestMatchers("/api/auth/**", "/documents/**", "/admin/**", "/user/**").permitAll() 
                // All other requests must be authenticated
                .anyRequest().authenticated()
            );

        return http.build();
    }

    // Modern way to expose the AuthenticationManager bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // A basic CORS configuration. Make sure the origin URL is correct.
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Make sure this matches your React app's URL (e.g., http://localhost:5173 or http://localhost:3000)
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000")); 
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "x-auth-token"));
        configuration.setExposedHeaders(List.of("x-auth-token"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

