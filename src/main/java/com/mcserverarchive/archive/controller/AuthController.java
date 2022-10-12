package com.mcserverarchive.archive.controller;

import com.mcserverarchive.archive.dtos.out.UsernameDto;
import com.mcserverarchive.archive.model.Account;
import com.mcserverarchive.archive.model.ERole;
import com.mcserverarchive.archive.model.Role;
import com.mcserverarchive.archive.model.Token;
import com.mcserverarchive.archive.payload.request.LoginRequest;
import com.mcserverarchive.archive.payload.request.SignupRequest;
import com.mcserverarchive.archive.payload.response.MessageResponse;
import com.mcserverarchive.archive.repositories.AccountRepository;
import com.mcserverarchive.archive.repositories.RoleRepository;
import com.mcserverarchive.archive.repositories.TokenRepository;
import com.mcserverarchive.archive.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

//@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController()
//@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;
    private final TokenRepository tokenRepository;

    private final AccountRepository accountRepository;

    @Autowired
    RoleRepository roleRepository;

    @PostMapping("api/auth/signout")
    public ResponseEntity<?> logoutUser(@CookieValue(name = "user-cookie") String ct) {

        ResponseCookie cookie = ResponseCookie.from("user-cookie", "").httpOnly(true).maxAge(0).build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new MessageResponse("You've been signed out!"));
    }

    @PostMapping("api/auth/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response, @CookieValue(name = "user-cookie", required = false) String ct) {

        try {
            Account account = accountService.getAccountByUsername(loginRequest.getUsername());

            Token token = new Token(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1), "0.0.0.0", account);
            accountService.createToken(token);

            // TODO: try add the path to /auth/api or something
            ResponseCookie cookie = ResponseCookie.from("user-cookie", token.getToken()).httpOnly(true).maxAge(60000).build();

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid username or password"));
        }
    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
//        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
//        corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("http://localhost:3000")); // also tried *
//        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT", "OPTIONS", "PATCH", "DELETE"));
//        corsConfiguration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/api/auth/**", corsConfiguration);
//        return source;
//    }

    @Bean
    public WebMvcConfigurer corsConfigurer2() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/auth/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedOriginPatterns("http://localhost:3000")
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST");
            }
        };
    }

    @GetMapping("api/auth/info")
    public ResponseEntity<?> getAccountInfoFromToken(@CookieValue(name = "user-cookie") String ct) {

        System.out.println(ct);

        Optional<Token> optionalToken = tokenRepository.findByToken(ct);
        if (optionalToken.isEmpty()) return ResponseEntity.ok().body("Invalid token");

        Token token = optionalToken.get();

        return ResponseEntity.ok(new UsernameDto(token.getAccount().getUsername(), token.getAccount().getId()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (accountRepository.existsByUsernameEqualsIgnoreCase(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (accountRepository.existsByEmailEqualsIgnoreCase(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        Account account = new Account(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword());

        Set<String> strRoles = new HashSet<>(Collections.singletonList("USER"));
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin" -> {
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                    }
                    case "mod" -> {
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                    }
                    default -> {
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                    }
                }
            });
        }

        //account.setRoles(roles);
        accountRepository.save(account);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}