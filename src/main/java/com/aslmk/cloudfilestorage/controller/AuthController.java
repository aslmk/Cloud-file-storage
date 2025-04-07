package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.RegisterDto;
import com.aslmk.cloudfilestorage.exception.InvalidCredentialsException;
import com.aslmk.cloudfilestorage.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute("user", registerDto);
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") RegisterDto registerDto,
                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (!registerDto.getPassword().equals(registerDto.getPasswordMatch())) {
            throw new InvalidCredentialsException("Passwords do not match");
        }

        userService.save(registerDto);


        return "redirect:/auth/login";
    }

}
