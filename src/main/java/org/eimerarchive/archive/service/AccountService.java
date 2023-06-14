package org.eimerarchive.archive.service;

import lombok.RequiredArgsConstructor;
import org.eimerarchive.archive.config.exception.RestErrorCode;
import org.eimerarchive.archive.config.exception.RestException;
import org.eimerarchive.archive.dtos.in.auth.LoginRequest;
import org.eimerarchive.archive.dtos.in.auth.SignupRequest;
import org.eimerarchive.archive.dtos.out.ErrorResponse;
import org.eimerarchive.archive.model.Account;
import org.eimerarchive.archive.model.Settings;
import org.eimerarchive.archive.model.Token;
import org.eimerarchive.archive.model.enums.ERole;
import org.eimerarchive.archive.repositories.AccountRepository;
import org.eimerarchive.archive.repositories.RoleRepository;
import org.eimerarchive.archive.repositories.SettingsRepository;
import org.eimerarchive.archive.repositories.TokenRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    private final SettingsRepository settingsRepository;

    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return AccountDetailsImpl.build(this.accountRepository.findByUsernameEquals(username).orElseThrow(() ->
                new UsernameNotFoundException("User Not Found with username: " + username)));
    }

    public ResponseEntity<?> signin(LoginRequest loginRequest) {
        Optional<Account> optionalAccount = this.accountRepository.findByUsernameEquals(loginRequest.getUsername());

        if (optionalAccount.isEmpty()) {
            return ResponseEntity.badRequest().body(ErrorResponse.create(RestErrorCode.INVALID_USERNAME.getDescription()));
        }

        if (!BCrypt.checkpw(loginRequest.getPassword(), optionalAccount.get().getPassword())) {
            return ResponseEntity.badRequest().body(ErrorResponse.create(RestErrorCode.WRONG_DETAILS.getDescription()));
        }

        Token token = new Token(0, optionalAccount.get());
        createToken(token);

        ResponseCookie cookie = ResponseCookie.from("user-cookie", token.getToken().toString()).path("/").secure(true).httpOnly(false).maxAge(604800).domain("").build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    public ResponseEntity<?> signup(SignupRequest signUpRequest) {
        if (accountRepository.existsByUsernameEqualsIgnoreCase(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(ErrorResponse.create(RestErrorCode.USERNAME_NOT_AVAILABLE.getDescription()));
        }

        if (accountRepository.existsByEmailEqualsIgnoreCase(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(ErrorResponse.create(RestErrorCode.EMAIL_NOT_AVAILABLE.getDescription()));
        }

        // Create new user's account
        Account account = new Account(signUpRequest.getUsername(), signUpRequest.getEmail(), this.encoder.encode(signUpRequest.getPassword()), null);
        account.setRoles(new HashSet<>(Collections.singleton(roleRepository.findByName(ERole.ROLE_USER).get())));

        accountRepository.save(account);

        // TODO: I am not sure if this is the best way to make the settings, revisit this in the future
        // Create the settings, set and save them to the account
        Settings settings = new Settings("", account);
        account.setSettings(settings);
        settingsRepository.save(settings);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> signout() {
        ResponseCookie cookie = ResponseCookie.from("user-cookie", "").path("/").httpOnly(false).maxAge(0).sameSite("None").secure(true).domain("").build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    public void createToken(Token token) {
        this.tokenRepository.save(token);
    }
    public Account getAccount(long id) throws RestException {
        return this.accountRepository.findById(id).orElseThrow(() -> new RestException(RestErrorCode.ACCOUNT_NOT_FOUND));
    }

    public Account getAccountByUsername(String username) throws RestException {
        return this.accountRepository.findByUsernameEquals(username).orElseThrow(() -> new RestException(RestErrorCode.ACCOUNT_NOT_FOUND));
    }

    public boolean hasPermissionToUpload(String token) {
        if (!this.tokenRepository.existsByToken(UUID.fromString(token))) {
            return false;
        }

        Token tokenObj = this.tokenRepository.findByToken(UUID.fromString(token)).get();
        Account account = tokenObj.getAccount();
        //return account.getRole().equalsIgnoreCase("ROLE_UPLOAD") || account.getRole().equalsIgnoreCase("ROLE_ADMIN");
        return true;
    }
}
