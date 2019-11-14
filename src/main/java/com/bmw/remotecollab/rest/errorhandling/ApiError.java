package com.bmw.remotecollab.rest.errorhandling;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
class ApiError {
    final HttpStatus status;
    final LocalDateTime timestamp;
    final String message;
    final List<ApiSubError> errors;

    private ApiError(HttpStatus status, String message, List<ApiSubError> subErrors) {
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.errors = subErrors;
    }

    static ApiError forFieldValidation(@Validated @NonNull List<ApiSubError> subErrors) {
        return new ApiError(HttpStatus.BAD_REQUEST, "field validation error", subErrors);
    }

    static ApiError forFieldValidation(@Validated @NonNull ApiSubError... subErrors) {
        return forFieldValidation(Arrays.asList(subErrors));
    }


    /**
     * Interface to summarize all business errors which should be presented and explained to the caller.
     */
    interface ApiSubError {
    }
}