package com.mcserverarchive.archive.controller;

import com.mcserverarchive.archive.model.Account;
import com.mcserverarchive.archive.model.ERole;
import com.mcserverarchive.archive.model.Role;
import com.mcserverarchive.archive.model.Token;
import com.mcserverarchive.archive.payload.request.LoginRequest;
import com.mcserverarchive.archive.payload.request.SignupRequest;
import com.mcserverarchive.archive.payload.response.MessageResponse;
import com.mcserverarchive.archive.repositories.AccountRepository;
import com.mcserverarchive.archive.repositories.RoleRepository;
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
import java.util.HashSet;
import java.util.Set;

//@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
@RestController()
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    @Autowired
    RoleRepository roleRepository;

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        //ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        //return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
        //.body(new MessageResponse("You've been signed out!"));

        return ResponseEntity.ok().body(new MessageResponse("You've been signed out!"));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        try {
            Account account = accountService.getAccountByUsername(loginRequest.getUsername());

            Token token = new Token(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1), "0.0.0.0", account);
            accountService.createToken(token);

            // Return the token in a cookie
            // TODO: try add the path to /auth/api or something
            ResponseCookie cookie = ResponseCookie.from("user-cookie", token.getToken()).httpOnly(true).maxAge(60).build();

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
            //.header("Access-Control-Allow-Credentials", "true")
            //.header("Access-Control-Allow-Origin", "http://localhost:3000").build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid username or password"));
        }
    }

    @Bean
    public WebMvcConfigurer corsConfigurer2() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8080")
                        .allowedMethods("GET", "POST", "OPTIONS");
            }
        };
    }

    // TODO: make role server sided
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

        Set<String> strRoles = signUpRequest.getRole();
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