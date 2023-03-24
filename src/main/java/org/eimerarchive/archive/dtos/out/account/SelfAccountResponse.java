package org.eimerarchive.archive.dtos.out.account;

import org.eimerarchive.archive.model.Account;

import java.time.LocalDateTime;

public record SelfAccountResponse(long id, String username, String email, byte[] profilePicture, LocalDateTime joined) {

    public static SelfAccountResponse fromAccount(Account account) {
        return new SelfAccountResponse(account.getId(), account.getUsername(),
            account.getEmail(), null,
            account.getJoined());
    }
}
