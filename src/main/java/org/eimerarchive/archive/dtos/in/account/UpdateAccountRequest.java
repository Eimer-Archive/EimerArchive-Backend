package org.eimerarchive.archive.dtos.in.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAccountRequest {
    private String username;
    private String email;
}
