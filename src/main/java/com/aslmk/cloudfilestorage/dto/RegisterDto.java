package com.aslmk.cloudfilestorage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDto {
    private String username;
    private String password;
    private String passwordMatch;

}
