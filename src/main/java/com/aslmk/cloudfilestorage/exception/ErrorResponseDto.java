package com.aslmk.cloudfilestorage.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponseDto {
    private int statusCode;
    private String message;
}
