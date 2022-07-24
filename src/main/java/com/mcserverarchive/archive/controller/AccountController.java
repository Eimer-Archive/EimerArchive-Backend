package com.mcserverarchive.archive.controller;

import com.mcserverarchive.archive.config.exception.RestException;
import com.mcserverarchive.archive.dtos.in.account.CreateAccountRequest;
import com.mcserverarchive.archive.dtos.in.account.UpdateAccountRequest;
import com.mcserverarchive.archive.dtos.out.account.AccountDto;
import com.mcserverarchive.archive.dtos.out.account.SelfAccountDto;
import com.mcserverarchive.archive.model.Account;
import com.mcserverarchive.archive.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/register")
    public String signupSubmit(@RequestBody CreateAccountRequest accountRequest) throws RestException {
        Account account = this.accountService.register(accountRequest);

        return ""; // todo return a registration DTO of some sort, probably with a session token
    }

    // todo login with JWT

    @GetMapping("/details")
    public SelfAccountDto getSelfAccountDetails(Account account) {
        return SelfAccountDto.fromAccount(account);
    }

    @PatchMapping("/details")
    public SelfAccountDto updateAccountDetails(Account account, @RequestBody UpdateAccountRequest request,
                                               @RequestParam(value = "profilePicture", required = false) MultipartFile file) throws RestException {
        return SelfAccountDto.fromAccount(this.accountService.updateAccountDetails(account, request, file));
    }

    @GetMapping("/{id}/details")
    public AccountDto getAccountDetails(@PathVariable int id) throws RestException {
        return AccountDto.fromAccount(this.accountService.getAccount(id));
    }
}
