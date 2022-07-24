package com.mcserverarchive.archive.dtos.out;

import com.mcserverarchive.archive.dtos.out.account.AccountDto;

public record ProfileDto(int id, int totalDownloads,
                         AccountDto account) {

}
