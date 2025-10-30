package com.example.demo.repository;

import com.example.demo.model.House;
import java.util.List;
import java.util.Optional;

public interface HouseRepository {
    List<House> findAll(boolean includeDeleted);
    default List<House> findAll() {
        return findAll(false);
    }
    Optional<House> findById(Long id);
    House save(House house);
    void deleteById(Long id);
    default void delete(House house) {
        if (house != null && house.getId() != null) {
            deleteById(house.getId());
        }
    }
    void logicalDeleteById(Long id);
    void deleteMultiple(List<Long> ids);
    void logicalDeleteMultiple(List<Long> ids);
}