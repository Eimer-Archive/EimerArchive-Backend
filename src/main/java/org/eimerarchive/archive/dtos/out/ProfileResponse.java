package org.eimerarchive.archive.dtos.out;

import org.eimerarchive.archive.dtos.out.account.AccountResponse;

public record ProfileResponse(int id, int totalDownloads,
                              AccountResponse account) {

}
