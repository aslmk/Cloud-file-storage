package com.aslmk.cloudfilestorage;

import com.aslmk.cloudfilestorage.controller.FileController;
import com.aslmk.cloudfilestorage.dto.LoginDto;
import com.aslmk.cloudfilestorage.dto.RegisterDto;
import com.aslmk.cloudfilestorage.entity.UserEntity;
import com.aslmk.cloudfilestorage.exception.AuthenticationFailedException;
import com.aslmk.cloudfilestorage.exception.UserAlreadyExistsException;
import com.aslmk.cloudfilestorage.repository.MinioRepository;
import com.aslmk.cloudfilestorage.repository.UserRepository;
import com.aslmk.cloudfilestorage.s3.FileService;
import com.aslmk.cloudfilestorage.security.CustomUserDetails;
import com.aslmk.cloudfilestorage.service.AuthService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@Transactional
public class AuthServiceIT {

    @MockitoBean
    private FileController fileController;

    @MockitoBean
    private FileService fileService;

    @MockitoBean
    private MinioRepository minioRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("cloudFileStorage")
            .withUsername("postgres")
            .withPassword("postgre123");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Test
    void register_should_SaveUserToDbAndToSecurityContext() {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("test0");
        dto.setPassword("test123");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        authService.register(dto, request, response);

        Assertions.assertNotNull(userRepository.findByUsername("test0"));

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Assertions.assertNotNull(principal);
        Assertions.assertInstanceOf(CustomUserDetails.class, principal);
        UserEntity userFromDb = ((CustomUserDetails) principal).getUserEntity();
        Assertions.assertNotNull(userFromDb);
        Assertions.assertEquals("test0", userFromDb.getUsername());
    }

    @Test
    void register_should_ThrowUserAlreadyExistsException_when_UserWithGivenUsernameAlreadyExists() {
        RegisterDto dto1 = new RegisterDto();
        dto1.setUsername("test0");
        dto1.setPassword("test123");

        RegisterDto dto2 = new RegisterDto();
        dto2.setUsername("test0");
        dto2.setPassword("test123");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        authService.register(dto1, request, response);

        Assertions.assertNotNull(userRepository.findByUsername("test0"));

        Assertions.assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(dto2, request, response)
        );
    }

    @Test
    void authenticate_should_AuthenticateUserAndSaveUserToSecurityContext() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("test0");
        registerDto.setPassword("test123");

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("test0");
        loginDto.setPassword("test123");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        authService.register(registerDto, request, response);

        Assertions.assertNotNull(userRepository.findByUsername("test0"));

        SecurityContextHolder.clearContext();
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());

        authService.authenticate(loginDto, request, response);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Assertions.assertNotNull(principal);
        Assertions.assertInstanceOf(CustomUserDetails.class, principal);
        UserEntity userFromDb = ((CustomUserDetails) principal).getUserEntity();
        Assertions.assertNotNull(userFromDb);
        Assertions.assertEquals("test0", userFromDb.getUsername());
    }

    @Test
    void authenticate_should_ThrowAuthenticationFailedException_when_UserProvidesInvalidPassword() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("test0");
        registerDto.setPassword("test123");

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("test0");
        loginDto.setPassword("test12");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        authService.register(registerDto, request, response);

        Assertions.assertNotNull(userRepository.findByUsername("test0"));

        SecurityContextHolder.clearContext();
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());

        Assertions.assertThrows(
                AuthenticationFailedException.class,
                () -> authService.authenticate(loginDto, request, response));

        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
