package com.example.demo.service;

import com.example.demo.model.House;
import com.example.demo.repository.HouseRepository;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.OwnershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseService {

    private final HouseRepository houseRepository;
    private final OwnershipRepository ownershipRepository;
    private final CityRepository cityRepository;

    public List<House> getAll(String addressFilter, Double minSize, String sortBy) {
        Specification<House> spec = (root, q, cb) -> cb.conjunction();
        if (addressFilter != null && !addressFilter.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("address")), "%" + addressFilter.toLowerCase() + "%"));
        }
        if (minSize != null) {
            spec = spec.and((root, q, cb) -> cb.ge(root.get("size"), minSize));
        }

        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isBlank()) {
            sort = switch (sortBy) {
                case "address" -> Sort.by("address");
                case "size" -> Sort.by("size");
                default -> Sort.unsorted();
            };
        }
        return houseRepository.findAll(spec, sort);
    }

    public void save(House house) {
        if (house.getCityId() != null) {
            house.setCity(cityRepository.findById(house.getCityId()).orElse(null));
        }
        houseRepository.save(house);
    }

    public void update(House house) {
        houseRepository.save(house);
    }

    public void delete(Long id) {
        if (ownershipRepository.countByHouseId(id) > 0) {
            throw new IllegalStateException("Нельзя удалить: дом связан с владельцами");
        }
        houseRepository.deleteById(id);
    }

    public void logicalDelete(Long id) {
        House h = houseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Дом не найден"));
        h.setDeleted(true);
        houseRepository.save(h);
    }

    public void restore(Long id) {
        House h = houseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Дом не найден"));
        h.setDeleted(false);
        houseRepository.save(h);
    }

    public void logicalDeleteAll(List<Long> ids) {
        ids.forEach(this::logicalDelete);
    }

    public void deleteAll(List<Long> ids) {
        ids.forEach(this::delete);
    }
}