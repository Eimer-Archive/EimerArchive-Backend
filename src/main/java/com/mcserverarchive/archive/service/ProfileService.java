package com.mcserverarchive.archive.service;

import com.mcserverarchive.archive.config.exception.RestException;
import com.mcserverarchive.archive.dtos.out.ProfileDto;
import com.mcserverarchive.archive.dtos.out.account.AccountDto;
import com.mcserverarchive.archive.model.Account;
import com.mcserverarchive.archive.repositories.UpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UpdateRepository updateRepository;
    private final AccountService accountService;

    public ProfileDto getProfileDto(int userId) throws RestException {
        Account account = this.accountService.getAccount(userId);
        //int totalDownloads = this.updateRepository.getTotalAccountDownloads(userId).orElse(0);

        return new ProfileDto(account.getId(), 0, AccountDto.fromAccount(account));
    }
}
