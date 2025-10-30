package com.example.demo.service;

import com.example.demo.model.House;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HouseService {

    private final List<House> houses = new ArrayList<>();
    private Long nextId = 1L;

    public List<House> getAll(String addressFilter, Double minSize, String sortBy) {
        var filtered = houses.stream()
                .filter(h -> addressFilter == null || h.getAddress().toLowerCase().contains(addressFilter.toLowerCase()))
                .filter(h -> minSize == null || h.getSize() >= minSize)
                .collect(Collectors.toList());

        if (sortBy != null) {
            switch (sortBy) {
                case "address" -> filtered.sort((a, b) -> a.getAddress().compareToIgnoreCase(b.getAddress()));
                case "size" -> filtered.sort((a, b) -> Double.compare(a.getSize(), b.getSize()));
            }
        }
        return filtered;
    }

    public void save(House house) {
        if (house.getId() == null) {
            house.setId(nextId++);
            houses.add(house);
        } else {
            update(house);
        }
    }

    public void update(House house) {
        House existing = houses.stream()
                .filter(h -> h.getId().equals(house.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Дом не найден"));
        existing.setAddress(house.getAddress());
        existing.setSize(house.getSize());
    }

    public void delete(Long id) {
        House house = houses.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Дом не найден"));
        houses.remove(house);
    }

    public void logicalDelete(Long id) {
        House house = houses.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Дом не найден"));
        house.setDeleted(true);
    }

    public void restore(Long id) {
        House house = houses.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Дом не найден"));
        house.setDeleted(false);
    }

    public void logicalDeleteAll(List<Long> ids) {
        ids.forEach(this::logicalDelete);
    }

    public void deleteAll(List<Long> ids) {
        ids.forEach(this::delete);
    }
}