package org.eimerarchive.archive.config.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@RestControllerAdvice
@RequiredArgsConstructor
public class RestExceptionResponseHandler extends ResponseEntityExceptionHandler {

    private final ObjectMapper mapper;

    @ExceptionHandler(RestException.class)
    public void handle(HttpServletResponse response, RestException exception) throws IOException {
        if (!response.isCommitted()) {
            response.setStatus(exception.getErrorCode().getHttpStatus().value());
            this.mapper.writeValue(response.getWriter(), exception);
        }
    }

    // Could add an error ticketing system for unknown errors (5xx) thrown
}
