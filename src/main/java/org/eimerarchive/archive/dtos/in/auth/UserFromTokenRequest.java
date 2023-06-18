package org.eimerarchive.archive.dtos.in.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserFromTokenRequest {
    @NotBlank
    private String token;
}