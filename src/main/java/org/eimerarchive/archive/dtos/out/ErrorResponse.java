package org.eimerarchive.archive.dtos.out;

public record ErrorResponse(String error) {

    public static ErrorResponse create(String error) {
        return new ErrorResponse(error);
    }
}
