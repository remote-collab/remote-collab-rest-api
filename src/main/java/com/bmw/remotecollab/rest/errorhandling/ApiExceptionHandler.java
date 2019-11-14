package com.bmw.remotecollab.rest.errorhandling;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(MethodArgumentNotValidException ex) {
        final List<ApiError.ApiSubError> subErrors = ex.getBindingResult().getFieldErrors().stream().
                map(ApiExceptionHandler::toApiSubError).collect(Collectors.toList());
        return ApiError.forFieldValidation(subErrors);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(MethodArgumentTypeMismatchException ex) {
        ApiValidationError e = new ApiValidationError();
        e.setField(ex.getName());
        e.setInvalidValue(ex.getValue());
        e.setMessage("Parameter could not be mapped to " + ex.getRequiredType());
        return ApiError.forFieldValidation(e);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(ConstraintViolationException exception) {
        final List<ApiError.ApiSubError> subErrors = exception.getConstraintViolations().stream()
                .map(ApiExceptionHandler::toApiSubError).collect(Collectors.toList());
        return ApiError.forFieldValidation(subErrors);
    }

    private static ApiError.ApiSubError toApiSubError(final FieldError error) {
        ApiValidationError e = new ApiValidationError();
        e.setField(error.getField());
        e.setInvalidValue(error.getRejectedValue());
        e.setMessage(error.getDefaultMessage());

        return e;
    }

    private static ApiError.ApiSubError toApiSubError(final ConstraintViolation violation) {
        String field = null;
        try {
            final String[] paths = violation.getPropertyPath().toString().split("\\.");
            field = paths[paths.length - 1];
        } catch (Exception e) {
            // nothing, field stays null
        }

        ApiValidationError e = new ApiValidationError();
        e.setField(field);
        e.setInvalidValue(violation.getInvalidValue());
        e.setMessage(violation.getMessage());
        return e;
    }
}