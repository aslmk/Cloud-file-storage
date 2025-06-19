package com.aslmk.cloudfilestorage.service;

import com.aslmk.cloudfilestorage.dto.RegisterDto;
import com.aslmk.cloudfilestorage.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {

    UserEntity saveUser(RegisterDto user);

    Optional<UserEntity> findByUsername(String username);
}
