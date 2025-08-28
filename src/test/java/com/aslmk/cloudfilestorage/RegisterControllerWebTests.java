package com.aslmk.cloudfilestorage;

import com.aslmk.cloudfilestorage.controller.RegisterController;
import com.aslmk.cloudfilestorage.dto.RegisterDto;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(RegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(NoRedisSessionTestConfig.class)
public class RegisterControllerWebTests {

    private static final String REGISTER_URL = "/auth/register";
    private static final String HOME_URL = "/home";
    private static final String MODEL_ATTRIBUTE_NAME = "user";
    private static final String REGISTER_PAGE_VIEW_NAME = "register";

    private static final String REGISTER_DTO_USERNAME_FIELD = "username";
    private static final String REGISTER_DTO_PASSWORD_FIELD = "password";
    private static final String REGISTER_DTO_PASSWORD_MATCH_FIELD = "passwordMatch";

    @MockitoBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    private RegisterDto registerDto;

    @BeforeEach
    void init() {
        registerDto = new RegisterDto();
        registerDto.setUsername("test1");
        registerDto.setPassword("test123");
        registerDto.setPasswordMatch("test123");
    }

    @Test
    void shouldRedirectToLoginPage_whenUserRegisteredSuccessfully() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                .flashAttr(MODEL_ATTRIBUTE_NAME, registerDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL));

        Mockito.verify(authService).register(Mockito.eq(registerDto),
                Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class));
    }

    @Test
    void shouldRedirectToRegisterPage_whenPasswordsDoNotMatch() throws Exception {
        registerDto.setPasswordMatch("test1435");

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                        .flashAttr(MODEL_ATTRIBUTE_NAME, registerDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REGISTER_URL))
                .andExpect(MockMvcResultMatchers.flash()
                        .attribute("error", "Passwords do not match"));
    }

    @Test
    void shouldReturnRegisterPage_whenUsernameFieldIsNull() throws Exception {
        registerDto.setUsername(null);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                        .flashAttr(MODEL_ATTRIBUTE_NAME, registerDto))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME, REGISTER_DTO_USERNAME_FIELD)
                ).andExpect(MockMvcResultMatchers.view().name(REGISTER_PAGE_VIEW_NAME));
    }

    @Test
    void shouldReturnRegisterPage_whenUsernameFieldIsEmpty() throws Exception {
        registerDto.setUsername("");

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                        .flashAttr(MODEL_ATTRIBUTE_NAME, registerDto))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME, REGISTER_DTO_USERNAME_FIELD)
                ).andExpect(MockMvcResultMatchers.view().name(REGISTER_PAGE_VIEW_NAME));
    }

    @Test
    void shouldReturnRegisterPage_whenPasswordFieldIsNull() throws Exception {
        registerDto.setPassword(null);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                        .flashAttr(MODEL_ATTRIBUTE_NAME, registerDto))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME, REGISTER_DTO_PASSWORD_FIELD)
                ).andExpect(MockMvcResultMatchers.view().name(REGISTER_PAGE_VIEW_NAME));
    }

    @Test
    void shouldReturnRegisterPage_whenPasswordFieldIsEmpty() throws Exception {
        registerDto.setPassword("");

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                        .flashAttr(MODEL_ATTRIBUTE_NAME, registerDto))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME, REGISTER_DTO_PASSWORD_FIELD)
                ).andExpect(MockMvcResultMatchers.view().name(REGISTER_PAGE_VIEW_NAME));
    }

    @Test
    void shouldReturnRegisterPage_whenPasswordMatchFieldIsNull() throws Exception {
        registerDto.setPasswordMatch(null);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                        .flashAttr(MODEL_ATTRIBUTE_NAME, registerDto))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME, REGISTER_DTO_PASSWORD_MATCH_FIELD)
                ).andExpect(MockMvcResultMatchers.view().name(REGISTER_PAGE_VIEW_NAME));
    }

    @Test
    void shouldReturnRegisterPage_whenPasswordMatchFieldIsEmpty() throws Exception {
        registerDto.setPasswordMatch("");

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                        .flashAttr(MODEL_ATTRIBUTE_NAME, registerDto))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME, REGISTER_DTO_PASSWORD_MATCH_FIELD)
                ).andExpect(MockMvcResultMatchers.view().name(REGISTER_PAGE_VIEW_NAME));
    }

    @Test
    void shouldReturnRegisterPage_whenAllFieldsOfRegisterDtoIsNull() throws Exception {
        registerDto.setUsername(null);
        registerDto.setPassword(null);
        registerDto.setPasswordMatch(null);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                        .flashAttr(MODEL_ATTRIBUTE_NAME, registerDto))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME,
                                REGISTER_DTO_USERNAME_FIELD,
                                REGISTER_DTO_PASSWORD_FIELD,
                                REGISTER_DTO_PASSWORD_MATCH_FIELD)
                ).andExpect(MockMvcResultMatchers.view().name(REGISTER_PAGE_VIEW_NAME));
    }

    @Test
    void shouldReturnRegisterPage_whenAllFieldsOfRegisterDtoIsEmpty() throws Exception {
        registerDto.setUsername("");
        registerDto.setPassword("");
        registerDto.setPasswordMatch("");

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                        .flashAttr(MODEL_ATTRIBUTE_NAME, registerDto))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attributeHasFieldErrors(MODEL_ATTRIBUTE_NAME,
                                REGISTER_DTO_USERNAME_FIELD,
                                REGISTER_DTO_PASSWORD_FIELD,
                                REGISTER_DTO_PASSWORD_MATCH_FIELD)
                ).andExpect(MockMvcResultMatchers.view().name(REGISTER_PAGE_VIEW_NAME));
    }

    @Test
    void shouldReturnRegisterPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REGISTER_URL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists(MODEL_ATTRIBUTE_NAME))
                .andExpect(MockMvcResultMatchers.view().name(REGISTER_PAGE_VIEW_NAME));
    }
}
