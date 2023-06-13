package org.eimerarchive.archive.config.security;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final DaoAuthenticationProvider authProvider;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(this.authProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests()
                .requestMatchers("/**").permitAll() // TODO: make this only allow certain paths
                .anyRequest().authenticated().and()
                .httpBasic();
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration authConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        authConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
        authConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
        authConfiguration.setAllowedMethods(List.of("GET", "POST"));
        authConfiguration.setAllowCredentials(true);
        source.registerCorsConfiguration("/api/auth/**", authConfiguration);

        CorsConfiguration accountConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        accountConfiguration.setAllowedOrigins(Collections.singletonList("*"));
        accountConfiguration.setAllowedMethods(List.of("GET", "POST"));
        source.registerCorsConfiguration("/api/info**", accountConfiguration);

        CorsConfiguration archiveConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        archiveConfiguration.setAllowedOrigins(Collections.singletonList("*"));
        archiveConfiguration.setAllowedMethods(List.of("GET", "POST"));
        source.registerCorsConfiguration("/api/archive/**", archiveConfiguration);

        CorsConfiguration fileConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        fileConfiguration.setAllowedOrigins(Collections.singletonList("*"));
        fileConfiguration.setAllowedMethods(List.of("GET", "POST"));
        source.registerCorsConfiguration("/api/file/**", fileConfiguration);

        return source;
    }
}