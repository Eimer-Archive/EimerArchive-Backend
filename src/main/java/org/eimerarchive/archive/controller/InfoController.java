package org.eimerarchive.archive.controller;

import org.eimerarchive.archive.config.exception.RestErrorCode;
import org.eimerarchive.archive.dtos.in.UserFromTokenRequest;
import org.eimerarchive.archive.dtos.out.ErrorResponse;
import org.eimerarchive.archive.dtos.out.UsernameResponse;
import org.eimerarchive.archive.model.Role;
import org.eimerarchive.archive.model.Token;
import org.eimerarchive.archive.model.enums.ERole;
import org.eimerarchive.archive.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InfoController {

    private final TokenRepository tokenRepository;

    @PostMapping("info")
    public ResponseEntity<?> getAccountInfoFromToken(@RequestBody UserFromTokenRequest dto) {

        Optional<Token> optionalToken = tokenRepository.findByToken(UUID.fromString(dto.getToken()));
        if (optionalToken.isEmpty()) return ResponseEntity.badRequest().body(ErrorResponse.create(RestErrorCode.FORBIDDEN.getDescription()));

        Token token1 = optionalToken.get();

        return ResponseEntity.ok(new UsernameResponse(token1.getAccount().getUsername(), token1.getAccount().getRoles().stream()
                .map(Role::getName)
                .map(ERole::name)
                .collect(Collectors.toList()), token1.getAccount().getId()));
    }

    @Bean
    public WebMvcConfigurer infoCorsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/info**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST");
            }
        };
    }
}
