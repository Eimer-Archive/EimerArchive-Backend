package org.eimerarchive.archive.service;

import org.eimerarchive.archive.config.custom.SiteConfig;
import org.eimerarchive.archive.config.exception.RestErrorCode;
import org.eimerarchive.archive.config.exception.RestException;
import org.eimerarchive.archive.dtos.in.account.CreateAccountRequest;
import org.eimerarchive.archive.dtos.in.account.UpdateAccountRequest;
import org.eimerarchive.archive.model.Account;
import org.eimerarchive.archive.model.Token;
import org.eimerarchive.archive.repositories.AccountRepository;
import org.eimerarchive.archive.repositories.TokenRepository;
import org.eimerarchive.archive.util.ImageUtil;
import org.eimerarchive.archive.util.ValidationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;

    private final SiteConfig siteConfig;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return accountRepository.findByUsernameEqualsIgnoreCase(s).orElseThrow(() -> new UsernameNotFoundException("User not found"));
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

        Account account = new Account(username, email, request.getPassword());
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
        return account.getRole().equalsIgnoreCase("ROLE_UPLOAD") || account.getRole().equalsIgnoreCase("ROLE_ADMIN");
    }

    // todo will there be concurrency issues with performing this all in one update?
    public Account updateAccountDetails(Account account, UpdateAccountRequest request, MultipartFile file) throws RestException {
        if (file != null) {
            if (file.getContentType() == null || !file.getContentType().contains("image")) throw new RestException(RestErrorCode.WRONG_FILE_TYPE);
            if (file.getSize() > this.siteConfig.getMaxUploadSize().toBytes()) throw new RestException(RestErrorCode.FILE_TOO_LARGE);

            account.setProfilePicture(ImageUtil.handleImage(file));
        }
        if (request.getEmail() != null) {
            String email = request.getEmail();
            if (!ValidationHelper.isEmailValid(email)) throw new RestException(RestErrorCode.INVALID_EMAIL);
            if (this.accountRepository.existsByEmailEqualsIgnoreCase(email)) throw new RestException(RestErrorCode.EMAIL_NOT_AVAILABLE);
            account.setEmail(email);
        }
        if (request.getUsername() != null) {
            String username = request.getUsername();
            if (!ValidationHelper.isUsernameValid(username)) throw new RestException(RestErrorCode.INVALID_USERNAME);
            if (this.accountRepository.existsByUsernameEqualsIgnoreCase(username)) throw new RestException(RestErrorCode.USERNAME_NOT_AVAILABLE);
            account.setUsername(username);
        }
        this.accountRepository.save(account);
        return account;
    }
}
