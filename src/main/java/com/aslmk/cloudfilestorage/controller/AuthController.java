package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.LoginDto;
import com.aslmk.cloudfilestorage.exception.AuthenticationFailedException;
import com.aslmk.cloudfilestorage.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new LoginDto());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("user") LoginDto loginDto,
                        BindingResult bindingResult,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            authService.authenticate(loginDto, request, response);
            session.setAttribute("username", loginDto.getUsername());
            return "redirect:/home";
        } catch (AuthenticationFailedException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/login";
        }
    }
}
