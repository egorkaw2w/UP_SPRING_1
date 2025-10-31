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

import java.util.ArrayList;
import java.util.List;

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
    @Column(nullable = false, length = 50)
    private String name;
    
    @Min(value = 0, message = "Возраст не может быть отрицательным")
    @Column(nullable = false)
    private int age;
    
    @NotBlank(message = "Пол не может быть пустым")
    @Size(min = 1, max = 1, message = "Пол должен быть 'M' или 'F'")
    @Pattern(regexp = "^[MF]$", message = "Пол: M или F")
    @Column(nullable = false, length = 1)
    private String gender;
    
    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<House> houses = new ArrayList<>();
}









