package org.eimerarchive.archive.dtos.in.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eimerarchive.archive.validation.ValidPassword;

public class SignupRequest {
    @NotBlank
    @Size(min = 4, max = 20)
    @Pattern.List({
            // Ascii Only
            @Pattern(regexp = "^((?![^\\x00-\\x7F]).)*$"),
            // We do not want people creating usernames like XxAnalPvP69xX
            @Pattern(regexp = "^((?!xx).)*$", flags = Pattern.Flag.CASE_INSENSITIVE)
    })
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 40)
    @ValidPassword
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}