package com.example.demo.controller.api;

import com.example.demo.dto.HouseDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.City;
import com.example.demo.model.House;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.HouseRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/houses")
@RequiredArgsConstructor
public class HouseRestController {
    private final HouseRepository houseRepository;
    private final CityRepository cityRepository;

    @GetMapping
    public List<HouseDto> list() {
        return houseRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public HouseDto get(@PathVariable Long id) {
        House h = houseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Дом не найден: " + id));
        return toDto(h);
    }

    @PostMapping
    public ResponseEntity<HouseDto> create(@Valid @RequestBody HouseDto dto) {
        House h = new House();
        h.setAddress(dto.getAddress());
        h.setSize(dto.getSize());
        if (dto.getCityId() != null) {
            City c = cityRepository.findById(dto.getCityId()).orElseThrow(() -> new ResourceNotFoundException("Город не найден: " + dto.getCityId()));
            h.setCity(c);
        }
        House saved = houseRepository.save(h);
        return ResponseEntity.created(URI.create("/api/houses/" + saved.getId())).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public HouseDto update(@PathVariable Long id, @Valid @RequestBody HouseDto dto) {
        House h = houseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Дом не найден: " + id));
        h.setAddress(dto.getAddress());
        h.setSize(dto.getSize());
        if (dto.getCityId() != null) {
            City c = cityRepository.findById(dto.getCityId()).orElseThrow(() -> new ResourceNotFoundException("Город не найден: " + dto.getCityId()));
            h.setCity(c);
        } else {
            h.setCity(null);
        }
        return toDto(houseRepository.save(h));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        House h = houseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Дом не найден: " + id));
        houseRepository.delete(h);
    }

    private HouseDto toDto(House h) {
        HouseDto dto = new HouseDto();
        dto.setId(h.getId());
        dto.setAddress(h.getAddress());
        dto.setSize(h.getSize());
        dto.setCityId(h.getCity() != null ? h.getCity().getId() : null);
        return dto;
    }
}

