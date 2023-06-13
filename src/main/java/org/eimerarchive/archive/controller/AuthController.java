package org.eimerarchive.archive.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.eimerarchive.archive.dtos.in.LoginRequest;
import org.eimerarchive.archive.dtos.in.SignupRequest;
import org.eimerarchive.archive.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;

    @PostMapping("signout")
    public ResponseEntity<?> logoutUser() {
        return this.accountService.signout();
    }

    @PostMapping("signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return this.accountService.signin(loginRequest);
    }

    @PostMapping("signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return this.accountService.signup(signUpRequest);
    }
}