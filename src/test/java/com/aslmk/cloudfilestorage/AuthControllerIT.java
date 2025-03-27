package com.aslmk.cloudfilestorage;

import com.aslmk.cloudfilestorage.dto.LoginDto;
import com.aslmk.cloudfilestorage.dto.RegisterDto;
import com.aslmk.cloudfilestorage.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
public class AuthControllerIT {

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

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRedirectToLoginPage_whenUserRegisteredSuccessfully() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .param("username", "test1")
                .param("password", "test123")
                .param("passwordMatch", "test123")
                .flashAttr("user", new RegisterDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));

        Assertions.assertTrue(userService.findByUsername("test1").isPresent());
    }

    @Test
    void shouldRedirectToHomePage_whenUserLoggedInSuccessfully() throws Exception {

        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("test1");
        registerDto.setPassword("test123");
        registerDto.setPasswordMatch("test123");
        userService.save(registerDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .param("username", "test1")
                .param("password", "test123")
                .flashAttr("user", new LoginDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

}
