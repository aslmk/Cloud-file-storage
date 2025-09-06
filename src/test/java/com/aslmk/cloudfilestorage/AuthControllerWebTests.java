package com.aslmk.cloudfilestorage;

import com.aslmk.cloudfilestorage.controller.AuthController;
import com.aslmk.cloudfilestorage.dto.LoginDto;
import com.aslmk.cloudfilestorage.exception.AuthenticationFailedException;
import com.aslmk.cloudfilestorage.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles("test")
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(NoRedisSessionTestConfig.class)
public class AuthControllerWebTests {

    private static final String LOGIN_URL = "/auth/login";
    private static final String HOME_URL = "/home";
    private static final String MODEL_ATTRIBUTE_NAME = "user";
    private static final String LOGIN_PAGE_VIEW_NAME = "login";

    private static final String LOGIN_DTO_USERNAME_FIELD = "username";
    private static final String LOGIN_DTO_PASSWORD_FIELD = "password";

    @MockitoBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    private LoginDto loginDto;

    @BeforeEach
    void init() {
        loginDto = new LoginDto();
        loginDto.setUsername("test0");
        loginDto.setPassword("test123");
    }

    @Test
    void should_redirectToHomePage_when_userLoggedInSuccessfully() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                .flashAttr(MODEL_ATTRIBUTE_NAME, loginDto))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(HOME_URL)
                );

        Mockito.verify(authService).authenticate(Mockito.eq(loginDto),
                Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class)
        );
    }

    @Test
    void should_returnLoginPage_when_usernameIsNull() throws Exception {
        loginDto.setUsername(null);

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                .flashAttr(MODEL_ATTRIBUTE_NAME, loginDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME, LOGIN_DTO_USERNAME_FIELD))
                .andExpect(MockMvcResultMatchers.view().name(LOGIN_PAGE_VIEW_NAME));
    }

    @Test
    void should_returnLoginPage_when_passwordIsNull() throws Exception {
        loginDto.setPassword(null);

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                .flashAttr(MODEL_ATTRIBUTE_NAME, loginDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME, LOGIN_DTO_PASSWORD_FIELD))
                .andExpect(MockMvcResultMatchers.view().name(LOGIN_PAGE_VIEW_NAME));
    }

    @Test
    void should_returnLoginPage_when_passwordIsEmpty() throws Exception {
        loginDto.setPassword("");

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                        .flashAttr(MODEL_ATTRIBUTE_NAME, loginDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME, LOGIN_DTO_PASSWORD_FIELD))
                .andExpect(MockMvcResultMatchers.view().name(LOGIN_PAGE_VIEW_NAME));
    }

    @Test
    void should_returnLoginPage_when_usernameIsEmpty() throws Exception {
        loginDto.setUsername("");

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                        .flashAttr(MODEL_ATTRIBUTE_NAME, loginDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME, LOGIN_DTO_USERNAME_FIELD))
                .andExpect(MockMvcResultMatchers.view().name(LOGIN_PAGE_VIEW_NAME));
    }

    @Test
    void should_redirectToLoginPage_when_userProvidesWrongCredentials() throws Exception {
        loginDto.setUsername("wrong");

        Mockito.doThrow(new AuthenticationFailedException("Invalid username or password"))
                .when(authService).authenticate(Mockito.eq(loginDto),
                        Mockito.any(HttpServletRequest.class), Mockito.any(HttpServletResponse.class));

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                .flashAttr(MODEL_ATTRIBUTE_NAME, loginDto))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(LOGIN_URL))
                .andExpect(MockMvcResultMatchers.flash()
                        .attribute("error", "Invalid username or password"));
    }
}
