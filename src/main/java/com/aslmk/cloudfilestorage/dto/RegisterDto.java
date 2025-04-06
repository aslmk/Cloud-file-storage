package com.aslmk.cloudfilestorage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDto {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must contain at least from 3 to 20 symbols")
    private String username;
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @NotBlank(message = "Please confirm your password")
    private String passwordMatch;

}
