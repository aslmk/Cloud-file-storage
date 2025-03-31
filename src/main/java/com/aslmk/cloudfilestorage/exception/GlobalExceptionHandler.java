package com.aslmk.cloudfilestorage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public String invalidCredentialsHandler(InvalidCredentialsException e, Model model) {

        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setMessage(e.getMessage());
        errorResponseDto.setStatusCode(HttpStatus.NOT_FOUND.value());

        model.addAttribute("errorDto", errorResponseDto);
        return "error";
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public String usernameNotFoundHandler(UsernameNotFoundException e, Model model) {

        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setMessage(e.getMessage());
        errorResponseDto.setStatusCode(HttpStatus.NOT_FOUND.value());

        model.addAttribute("errorDto", errorResponseDto);
        return "error";
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public String userAlreadyExistsHandler(UserAlreadyExistsException e, Model model) {

        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setMessage(e.getMessage());
        errorResponseDto.setStatusCode(HttpStatus.CONFLICT.value());

        model.addAttribute("errorDto", errorResponseDto);
        return "error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String noHandlerFoundExceptionHandler(NoHandlerFoundException e, Model model) {

        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setMessage("Page not found: " + e.getRequestURL());
        errorResponseDto.setStatusCode(HttpStatus.NOT_FOUND.value());

        model.addAttribute("errorDto", errorResponseDto);
        return "error";
    }

}
