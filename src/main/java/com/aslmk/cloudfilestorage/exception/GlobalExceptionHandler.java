package com.aslmk.cloudfilestorage.exception;

import io.minio.errors.*;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String invalidCredentialsHandler(InvalidCredentialsException e, Model model) {
        handleException(e.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                model);
        return "error";
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String usernameNotFoundHandler(UsernameNotFoundException e, Model model) {
        handleException(e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                model);
        return "error";
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String userAlreadyExistsHandler(UserAlreadyExistsException e, Model model) {
        handleException(e.getMessage(),
                HttpStatus.CONFLICT.value(),
                model);
        return "error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noHandlerFoundExceptionHandler(NoHandlerFoundException e, Model model) {
        handleException("Page not found: " + e.getRequestURL(),
                HttpStatus.NOT_FOUND.value(),
                model);
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exceptionHandler(Model model) {
        handleException("Internal Server Error. Try again",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                model);
        return "error";
    }

    @ExceptionHandler({ServerException.class,
            InsufficientDataException.class,
            ErrorResponseException.class,
            IOException.class,
            NoSuchAlgorithmException.class,
            InvalidKeyException.class,
            InvalidResponseException.class,
            XmlParserException.class,
            InternalException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String minioExceptionHandler(Model model) {
        handleException("Storage error. Try again",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                model);
        return "error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String accessDeniedExceptionHandler(AccessDeniedException e, Model model) {
        handleException(e.getMessage(), HttpStatus.FORBIDDEN.value(), model);
        return "error";
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badRequestExceptionHandler(BadRequestException e, Model model) {
        handleException(e.getMessage(), HttpStatus.BAD_REQUEST.value(), model);
        return "error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String validationExceptionsHandler(MethodArgumentNotValidException e, Model model) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach((error) -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        StringBuilder errorMessage = new StringBuilder("Validation errors: ");
        boolean first = true;
        for (Map.Entry<String, String> entry : errors.entrySet()) {
            if (!first) {
                errorMessage.append("; ");
            }
            errorMessage.append(entry.getValue()).append("\n");
            first = false;
        }

        handleException(errorMessage.toString(), HttpStatus.BAD_REQUEST.value(), model);
        return "error";
    }

    private void handleException(String message, int statusCode, Model model) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setMessage(message);
        errorResponseDto.setStatusCode(statusCode);
        model.addAttribute("errorDto", errorResponseDto);
    }
}
