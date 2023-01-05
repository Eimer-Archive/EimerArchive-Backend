package com.mcserverarchive.archive.config.exception;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

public enum RestErrorCode {
    INVALID_USERNAME(HttpStatus.BAD_REQUEST, "auth", 1, "Invalid username"),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "auth", 2, "Invalid email"),
    USERNAME_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "auth", 3, "Username not available"),
    EMAIL_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "auth", 4, "Email not available"),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "auth", 100, "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "auth", 101, "Forbidden"),

    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "data", 100, "Account not found"),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "data", 101, "Report not found"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "data", 102, "Resource not found"),
    RESOURCE_UPDATE_NOT_FOUND(HttpStatus.NOT_FOUND, "data", 103, "Resource update not found"),
    DOWNLOAD_NOT_FOUND(HttpStatus.NOT_FOUND, "data", 104, "Download not found"),

    WRONG_FILE_TYPE(HttpStatus.BAD_REQUEST, "data", 2, "Wrong file type"),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "data", 3, "File too large"),
    PAGE_SIZE_TOO_LARGE(HttpStatus.BAD_REQUEST, "data", 4, "Page size too large"),
    REQUIRED_ARGUMENTS_MISSING(HttpStatus.BAD_REQUEST, "data", 5, "Required arguments missing"),

    TOO_MANY_RESOURCE_CREATIONS(HttpStatus.TOO_MANY_REQUESTS, "resource", 1, "Too many resource creations"),
    TOO_MANY_RESOURCE_UPDATES(HttpStatus.TOO_MANY_REQUESTS, "resource", 2, "Too many resource updates"),
    RESOURCE_NAME_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "resource", 3, "Resource name not available"),
    RESOURCE_SLUG_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "resource", 4, "Resource slug not available");

    private final HttpStatus httpStatus;
    private final String module;
    private final String description;
    private final int errorCode;

    RestErrorCode(HttpStatus httpStatus, String module, int errorCode, String description) {
        this.httpStatus = httpStatus;
        this.module = module;
        this.errorCode = errorCode;
        this.description = description;
    }

    @JsonIgnore
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public String getModule() {
        return this.module;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getDescription() {
        return this.description;
    }
}
