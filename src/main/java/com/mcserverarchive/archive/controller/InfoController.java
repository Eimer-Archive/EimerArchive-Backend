package com.mcserverarchive.archive.controller;

import com.mcserverarchive.archive.dtos.in.UserFromTokenDto;
import com.mcserverarchive.archive.dtos.out.UsernameDto;
import com.mcserverarchive.archive.model.Token;
import com.mcserverarchive.archive.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InfoController {

    private final TokenRepository tokenRepository;

    @PostMapping("info")
    public ResponseEntity<?> getAccountInfoFromToken(@RequestBody UserFromTokenDto dto) {

        Optional<Token> optionalToken = tokenRepository.findByToken(dto.getToken());
        if (optionalToken.isEmpty()) return ResponseEntity.ok().body("Invalid token");

        Token token1 = optionalToken.get();

        return ResponseEntity.ok(new UsernameDto(token1.getAccount().getUsername(), token1.getAccount().getId()));
    }
}
