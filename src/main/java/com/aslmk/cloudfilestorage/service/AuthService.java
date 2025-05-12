package com.aslmk.cloudfilestorage.service;

import com.aslmk.cloudfilestorage.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    void authenticate(LoginDto loginDto, HttpServletRequest request, HttpServletResponse response);
}
