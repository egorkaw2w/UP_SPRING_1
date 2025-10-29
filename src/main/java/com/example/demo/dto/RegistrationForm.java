package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationForm {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

    @NotBlank(message = "Подтверждение пароля не может быть пустым")
    private String confirmPassword;

    @Email(message = "Неверный email")
    private String email;
}

