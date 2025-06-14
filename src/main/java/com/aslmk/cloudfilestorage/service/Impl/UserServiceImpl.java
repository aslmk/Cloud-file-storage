package com.aslmk.cloudfilestorage.service.Impl;

import com.aslmk.cloudfilestorage.dto.RegisterDto;
import com.aslmk.cloudfilestorage.entity.UserEntity;
import com.aslmk.cloudfilestorage.exception.ServiceException;
import com.aslmk.cloudfilestorage.exception.UserAlreadyExistsException;
import com.aslmk.cloudfilestorage.mapper.UserMapper;
import com.aslmk.cloudfilestorage.repository.UserRepository;
import com.aslmk.cloudfilestorage.service.UserService;
import org.hibernate.exception.ConstraintViolationException;
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
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public void saveUser(RegisterDto user) throws UserAlreadyExistsException, ServiceException {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(userMapper.mapToUserEntity(user));
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException cve) {
                 String constraintName = cve.getConstraintName();

                if (constraintName == null) {
                    throw new ServiceException("Unknown database constraint exception: " + e.getMessage());
                }

                 if (constraintName.equals("users_username_key")) {
                     throw new UserAlreadyExistsException(
                             String.format("User %s already exists", user.getUsername())
                     );
                 }
            } else {
                throw new ServiceException("Unexpected error occurred while saving user: " + e.getMessage());
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
                .build();
    }
}
