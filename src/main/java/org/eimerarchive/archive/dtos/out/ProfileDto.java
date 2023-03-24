package org.eimerarchive.archive.dtos.out;

import org.eimerarchive.archive.dtos.out.account.AccountDto;

public record ProfileDto(int id, int totalDownloads,
                         AccountDto account) {

}
