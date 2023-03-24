package org.eimerarchive.archive.dtos.out;

import org.eimerarchive.archive.dtos.out.account.AccountResponse;

public record ProfileResponse(long id, int totalDownloads, AccountResponse account) {

}
