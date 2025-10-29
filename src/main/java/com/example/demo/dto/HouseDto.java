package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseDto {
    private Long id;

    @NotBlank(message = "Адрес не может быть пустым")
    @Size(min = 5, max = 100, message = "Адрес должен быть от 5 до 100 символов")
    private String address;

    @Min(value = 10, message = "Площадь должна быть не менее 10 кв.м")
    private double size;

    private Long cityId;
}

