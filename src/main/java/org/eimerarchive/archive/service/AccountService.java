package org.eimerarchive.archive.service;

import lombok.RequiredArgsConstructor;
import org.eimerarchive.archive.config.custom.SiteConfig;
import org.eimerarchive.archive.config.exception.RestErrorCode;
import org.eimerarchive.archive.config.exception.RestException;
import org.eimerarchive.archive.dtos.in.LoginRequest;
import org.eimerarchive.archive.dtos.in.SignupRequest;
import org.eimerarchive.archive.dtos.in.account.CreateAccountRequest;
import org.eimerarchive.archive.dtos.out.ErrorResponse;
import org.eimerarchive.archive.model.Account;
import org.eimerarchive.archive.model.Role;
import org.eimerarchive.archive.model.Token;
import org.eimerarchive.archive.model.enums.ERole;
import org.eimerarchive.archive.repositories.AccountRepository;
import org.eimerarchive.archive.repositories.RoleRepository;
import org.eimerarchive.archive.repositories.TokenRepository;
import org.eimerarchive.archive.util.ValidationHelper;
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
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final SiteConfig siteConfig;

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

        ResponseCookie cookie = ResponseCookie.from("user-cookie", token.getToken()).path("/").secure(true).httpOnly(false).maxAge(604800).domain("").build();

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

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> signout() {
        ResponseCookie cookie = ResponseCookie.from("user-cookie", "").path("/").httpOnly(false).maxAge(0).sameSite("None").secure(true).domain("").build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    public void createToken(Token token) {
        tokenRepository.save(token);
    }

    public Account register(CreateAccountRequest request) throws RestException {
        String username = request.getUsername();
        if (!ValidationHelper.isUsernameValid(username)) throw new RestException(RestErrorCode.INVALID_USERNAME);

        String email = request.getEmail();
        if (!ValidationHelper.isEmailValid(email)) throw new RestException(RestErrorCode.INVALID_EMAIL);

        if (this.accountRepository.existsByUsernameEqualsIgnoreCase(username)) throw new RestException(RestErrorCode.USERNAME_NOT_AVAILABLE);
        if (this.accountRepository.existsByEmailEqualsIgnoreCase(email)) throw new RestException(RestErrorCode.EMAIL_NOT_AVAILABLE);

        Account account = new Account(username, email, request.getPassword(), null);
        this.accountRepository.save(account);

        return account;
    }

    public Account getAccount(int id) throws RestException {
        return this.accountRepository.findById(id).orElseThrow(() -> new RestException(RestErrorCode.ACCOUNT_NOT_FOUND));
    }

    public Account getAccountByUsername(String username) throws RestException {
        return this.accountRepository.findByUsernameEquals(username).orElseThrow(() -> new RestException(RestErrorCode.ACCOUNT_NOT_FOUND));
    }

    public boolean hasPermissionToUpload(String token) {
        if (!this.tokenRepository.existsByToken(token)) {
            return false;
        }

        Token tokenObj = this.tokenRepository.findByToken(token).get();
        Account account = tokenObj.getAccount();
        //return account.getRole().equalsIgnoreCase("ROLE_UPLOAD") || account.getRole().equalsIgnoreCase("ROLE_ADMIN");
        return false;
    }

    // todo will there be concurrency issues with performing this all in one update?
//    public Account updateAccountDetails(Account account, UpdateAccountRequest request, MultipartFile file) throws RestException {
//        if (file != null) {
//            if (file.getContentType() == null || !file.getContentType().contains("image")) throw new RestException(RestErrorCode.WRONG_FILE_TYPE);
//            if (file.getSize() > this.siteConfig.getMaxUploadSize().toBytes()) throw new RestException(RestErrorCode.FILE_TOO_LARGE);
//
//            account.setProfilePicture(ImageUtil.handleImage(file));
//        }
//        if (request.getEmail() != null) {
//            String email = request.getEmail();
//            if (!ValidationHelper.isEmailValid(email)) throw new RestException(RestErrorCode.INVALID_EMAIL);
//            if (this.accountRepository.existsByEmailEqualsIgnoreCase(email)) throw new RestException(RestErrorCode.EMAIL_NOT_AVAILABLE);
//            account.setEmail(email);
//        }
//        if (request.getUsername() != null) {
//            String username = request.getUsername();
//            if (!ValidationHelper.isUsernameValid(username)) throw new RestException(RestErrorCode.INVALID_USERNAME);
//            if (this.accountRepository.existsByUsernameEqualsIgnoreCase(username)) throw new RestException(RestErrorCode.USERNAME_NOT_AVAILABLE);
//            account.setUsername(username);
//        }
//        this.accountRepository.save(account);
//        return account;
//    }
}
