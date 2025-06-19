package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.RegisterDto;
import com.aslmk.cloudfilestorage.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
public class RegisterController {

    private final AuthService authService;

    public RegisterController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute("user", registerDto);
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") RegisterDto registerDto,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (!registerDto.getPassword().equals(registerDto.getPasswordMatch())) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/auth/register";
        }

        authService.register(registerDto, request, response);

        return "redirect:/home";
    }

}
