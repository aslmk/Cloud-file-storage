package com.aslmk.cloudfilestorage.service.Impl;

import com.aslmk.cloudfilestorage.dto.LoginDto;
import com.aslmk.cloudfilestorage.dto.RegisterDto;
import com.aslmk.cloudfilestorage.entity.UserEntity;
import com.aslmk.cloudfilestorage.exception.AuthenticationFailedException;
import com.aslmk.cloudfilestorage.security.CustomUserDetails;
import com.aslmk.cloudfilestorage.service.AuthService;
import com.aslmk.cloudfilestorage.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final UserService userService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, SecurityContextRepository securityContextRepository, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.userService = userService;
    }

    public void authenticate(LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken
                .unauthenticated(loginDto.getUsername(), loginDto.getPassword()
                );

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            saveSecurityContext(authentication, request, response);
        } catch (BadCredentialsException e) {
            throw new AuthenticationFailedException("Invalid username or password");
        }
    }

    public void register(RegisterDto registerDto, HttpServletRequest request, HttpServletResponse response) {
        UserEntity user = userService.saveUser(registerDto);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken
                .authenticated(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        saveSecurityContext(authentication, request, response);
    }

    private void saveSecurityContext(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }
}
