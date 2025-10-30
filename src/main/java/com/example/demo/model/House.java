package com.example.demo.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class House {
    private Long id;
    @NotBlank(message = "Адрес не может быть пустым")
    @Size(min = 5, max = 100, message = "Адрес должен быть от 5 до 100 символов")
    private String address;
    @Min(value = 10, message = "Площадь должна быть не менее 10 кв.м")
    private double size;
    @Builder.Default
    private boolean deleted = false;
}