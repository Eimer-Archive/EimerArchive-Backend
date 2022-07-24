package com.mcserverarchive.archive.dtos.out.account;

import com.mcserverarchive.archive.model.Account;

import java.time.LocalDateTime;

public record AccountDto(int id, String username,
                         byte[] profilePicture, LocalDateTime joined) {

    public static AccountDto fromAccount(Account account) {
        return new AccountDto(account.getId(), account.getUsername(),
            account.getProfilePicture(), account.getJoined());
    }
}
