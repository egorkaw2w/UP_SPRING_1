package com.example.demo.controller;

import com.example.demo.dto.RegistrationForm;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                               BindingResult br,
                               RedirectAttributes ra,
                               Model model) {
        if (br.hasErrors()) {
            return "register";
        }
        try {
            userService.registerNewUser(form);
        } catch (IllegalArgumentException e) {
            model.addAttribute("registrationError", e.getMessage());
            return "register";
        }
        ra.addFlashAttribute("message", "Регистрация прошла успешно. Войдите в систему.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверные имя пользователя или пароль.");
        }
        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли.");
        }
        return "login";
    }
}

