package org.eimerarchive.archive.dtos.in.auth;

import lombok.Getter;

@Getter
public class UserFromTokenRequest {
    private String token;
}