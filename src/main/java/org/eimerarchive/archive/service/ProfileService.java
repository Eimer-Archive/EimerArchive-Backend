package org.eimerarchive.archive.service;

import org.eimerarchive.archive.config.exception.RestException;
import org.eimerarchive.archive.dtos.out.ProfileResponse;
import org.eimerarchive.archive.dtos.out.account.AccountResponse;
import org.eimerarchive.archive.model.Account;
import org.eimerarchive.archive.repositories.UpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UpdateRepository updateRepository;
    private final AccountService accountService;

    public ProfileResponse getProfileDto(int userId) throws RestException {
        Account account = this.accountService.getAccount(userId);
        //int totalDownloads = this.updateRepository.getTotalAccountDownloads(userId).orElse(0);

        return new ProfileResponse(account.getId(), 0, AccountResponse.fromAccount(account));
    }
}
