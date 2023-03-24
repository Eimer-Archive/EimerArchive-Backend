package org.eimerarchive.archive.dtos.out.account;

import org.eimerarchive.archive.model.Account;

import java.time.LocalDateTime;

public record AccountResponse(int id, String username,
                              byte[] profilePicture, LocalDateTime joined) {

    public static AccountResponse fromAccount(Account account) {
        return new AccountResponse(account.getId(), account.getUsername(),
            account.getProfilePicture(), account.getJoined());
    }
}
