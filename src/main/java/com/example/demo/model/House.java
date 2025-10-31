package com.example.demo.model;

import jakarta.persistence.*;
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
@Entity
@Table(name = "houses")
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Адрес не может быть пустым")
    @Size(min = 5, max = 100, message = "Адрес должен быть от 5 до 100 символов")
    @Column(nullable = false, length = 100)
    private String address;
    
    @Min(value = 10, message = "Площадь должна быть не менее 10 кв.м")
    @Column(nullable = false)
    private double size;
    
    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = true)
    private Person owner;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;
}