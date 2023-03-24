package org.eimerarchive.archive.controller;

import org.eimerarchive.archive.config.exception.RestErrorCode;
import org.eimerarchive.archive.dtos.in.UserFromTokenDto;
import org.eimerarchive.archive.dtos.out.ErrorDto;
import org.eimerarchive.archive.dtos.out.UsernameDto;
import org.eimerarchive.archive.model.Token;
import org.eimerarchive.archive.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InfoController {

    private final TokenRepository tokenRepository;

    @PostMapping("info")
    public ResponseEntity<?> getAccountInfoFromToken(@RequestBody UserFromTokenDto dto) {

        Optional<Token> optionalToken = tokenRepository.findByToken(dto.getToken());
        if (optionalToken.isEmpty()) return ResponseEntity.badRequest().body(ErrorDto.create(RestErrorCode.FORBIDDEN.getDescription()));

        Token token1 = optionalToken.get();

        return ResponseEntity.ok(new UsernameDto(token1.getAccount().getUsername(), token1.getAccount().getRole(), token1.getAccount().getId()));
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
