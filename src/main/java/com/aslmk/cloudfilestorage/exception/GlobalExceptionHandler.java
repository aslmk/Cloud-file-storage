package com.aslmk.cloudfilestorage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

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


    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String minioExceptionHandler(StorageException e, Model model) {
        handleException(e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                model);
        return "error";
    }


    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badRequestExceptionHandler(BadRequestException e, Model model) {
        handleException(e.getMessage(), HttpStatus.BAD_REQUEST.value(), model);
        return "error";
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String unauthorizedAccessExceptionHandler(UnauthorizedAccessException e, Model model) {
        handleException(e.getMessage(), HttpStatus.UNAUTHORIZED.value(), model);
        return "error";
    }

    private void handleException(String message, int statusCode, Model model) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setMessage(message);
        errorResponseDto.setStatusCode(statusCode);
        model.addAttribute("errorDto", errorResponseDto);
    }
}
