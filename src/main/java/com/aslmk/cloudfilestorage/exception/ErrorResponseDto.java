package com.aslmk.cloudfilestorage.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponseDto {
    private int statusCode;
    private String message;
    private LocalDateTime timestamp;
}
