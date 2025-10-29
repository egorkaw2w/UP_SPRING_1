package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
@Entity
@Table(name = "persons")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private String name;

    @Min(value = 0, message = "Возраст не может быть отрицательным")
    private int age;

    @ManyToOne(optional = true)
    @JoinColumn(name = "city_id")
    private City city;

    @NotBlank(message = "Пол не может быть пустым")
    @Size(min = 1, max = 1,  message = "Пол должен быть 'M' или 'F'")
    @Pattern(regexp = "^[MF]$", message = "Пол: M или F")
    private String gender;

    @Builder.Default
    private boolean deleted = false;

    @Transient
    private String cityName;
}









