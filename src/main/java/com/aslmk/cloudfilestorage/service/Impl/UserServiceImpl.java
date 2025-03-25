package com.aslmk.cloudfilestorage.service.Impl;

import com.aslmk.cloudfilestorage.dto.RegisterDto;
import com.aslmk.cloudfilestorage.entity.UserEntity;
import com.aslmk.cloudfilestorage.exception.UserAlreadyExistsException;
import com.aslmk.cloudfilestorage.repository.UserRepository;
import com.aslmk.cloudfilestorage.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(RegisterDto user) throws UserAlreadyExistsException {
        try {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(user.getUsername());
            userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
            userEntity.setEnabled(true);
            userRepository.save(userEntity);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("users_username_key")) {
                throw new UserAlreadyExistsException(
                        String.format("User %s already exists", user.getUsername())
                );
            }
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User '%s' not found", username)
        ));

        return User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .roles("USER")
                .disabled(!userEntity.getEnabled())
                .build();
    }
}
