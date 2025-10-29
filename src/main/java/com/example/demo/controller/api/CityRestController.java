package com.example.demo.controller.api;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.City;
import com.example.demo.repository.CityRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityRestController {
    private final CityRepository cityRepository;

    @GetMapping
    public List<City> list() {
        return cityRepository.findAll();
    }

    @GetMapping("/{id}")
    public City get(@PathVariable Long id) {
        return cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Город не найден: " + id));
    }

    @PostMapping
    public ResponseEntity<City> create(@Valid @RequestBody City city) {
        City saved = cityRepository.save(city);
        return ResponseEntity.created(URI.create("/api/cities/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public City update(@PathVariable Long id, @Valid @RequestBody City city) {
        City existing = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Город не найден: " + id));
        existing.setName(city.getName());
        return cityRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        City c = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Город не найден: " + id));
        cityRepository.delete(c);
    }
}

