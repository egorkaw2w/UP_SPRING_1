package com.example.demo.repository;

import com.example.demo.model.House;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class HouseRepositoryImpl implements HouseRepository {
    private final List<House> houses = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<House> findAll(boolean includeDeleted) {
        return houses.stream()
                .filter(h -> includeDeleted || !h.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<House> findById(Long id) {
        return houses.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst();
    }

    @Override
    public House save(House house) {
        if (house.getId() == null) {
            house.setId(idGenerator.getAndIncrement());
            houses.add(house);
            return house;
        } else {
            return findById(house.getId()).map(existing -> {
                existing.setAddress(house.getAddress());
                existing.setSize(house.getSize());
                return existing;
            }).orElseGet(() -> {
                houses.add(house);
                return house;
            });
        }
    }

    @Override
    public void deleteById(Long id) {
        houses.removeIf(h -> h.getId().equals(id));
    }

    @Override
    public void logicalDeleteById(Long id) {
        findById(id).ifPresent(h -> h.setDeleted(true));
    }

    @Override
    public void deleteMultiple(List<Long> ids) {
        houses.removeIf(h -> ids.contains(h.getId()));
    }

    @Override
    public void logicalDeleteMultiple(List<Long> ids) {
        houses.stream()
                .filter(h -> ids.contains(h.getId()))
                .forEach(h -> h.setDeleted(true));
    }
}