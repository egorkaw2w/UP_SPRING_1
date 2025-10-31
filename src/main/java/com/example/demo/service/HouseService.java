package com.example.demo.service;

import com.example.demo.model.House;
import com.example.demo.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseService {
    private final HouseRepository houseRepository;

    public List<House> getAll(String addressFilter, Double minSize, String sortBy) {
        var filtered = houseRepository.findAll().stream()
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

    @Transactional
    public House save(House house) {
        return houseRepository.save(house);
    }

    @Transactional
    public House update(House house) {
        House existing = houseRepository.findById(house.getId())
                .orElseThrow(() -> new IllegalArgumentException("Дом не найден"));
        existing.setAddress(house.getAddress());
        existing.setSize(house.getSize());
        if (house.getOwner() != null) {
            existing.setOwner(house.getOwner());
        }
        if (house.getCity() != null) {
            existing.setCity(house.getCity());
        }
        return houseRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) throws IllegalStateException {
        House house = houseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Дом не найден"));
        // Проверка связей: если дом принадлежит человеку, то при удалении связь будет удалена каскадно
        // Но можно добавить дополнительную проверку, если нужно
        houseRepository.delete(house);
    }

    @Transactional
    public void logicalDelete(Long id) {
        houseRepository.logicalDeleteById(id);
    }

    @Transactional
    public void restore(Long id) {
        houseRepository.restoreById(id);
    }

    @Transactional
    public void logicalDeleteAll(List<Long> ids) {
        houseRepository.logicalDeleteMultiple(ids);
    }

    @Transactional
    public void deleteAll(List<Long> ids) {
        for (Long id : ids) {
            delete(id);
        }
    }
}
