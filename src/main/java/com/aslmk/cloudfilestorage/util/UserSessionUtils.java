package com.aslmk.cloudfilestorage.util;

import com.aslmk.cloudfilestorage.entity.UserEntity;
import com.aslmk.cloudfilestorage.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserSessionUtils {
    private final UserService userService;

    public UserSessionUtils(UserService userService) {
        this.userService = userService;
    }

    public UserEntity getUserFromSession(HttpSession session) {
        String usernameSession = (String) session.getAttribute("username");

        return userService.findByUsername(usernameSession).orElseThrow(
                ()-> new UsernameNotFoundException(
                        String.format("User %s not found", usernameSession)
        ));
    }
}
