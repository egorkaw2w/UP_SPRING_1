package com.example.demo.service;

import com.example.demo.model.City;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.HouseRepository;
import com.example.demo.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepository;
    private final PersonRepository personRepository;
    private final HouseRepository houseRepository;

    public List<City> findAll() {
        return cityRepository.findAll();
    }

    public City findById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Город не найден"));
    }

    @Transactional
    public City save(City city) {
        // Проверка на уникальность имени
        if (cityRepository.existsByName(city.getName())) {
            throw new IllegalArgumentException("Город с таким названием уже существует");
        }
        return cityRepository.save(city);
    }

    @Transactional
    public City update(City city) {
        City existing = cityRepository.findById(city.getId())
                .orElseThrow(() -> new IllegalArgumentException("Город не найден"));
        
        // Проверка на уникальность имени (кроме текущего города)
        if (!existing.getName().equals(city.getName()) && cityRepository.existsByName(city.getName())) {
            throw new IllegalArgumentException("Город с таким названием уже существует");
        }
        
        existing.setName(city.getName());
        return cityRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) throws IllegalStateException {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Город не найден"));
        
        // Проверка связей: нельзя удалить City, если есть связанные Person или House
        if (personRepository.existsPersonsForCity(id)) {
            throw new IllegalStateException("Нельзя удалить город, в котором проживают люди. Сначала удалите или переместите людей.");
        }
        if (houseRepository.existsHousesForCity(id)) {
            throw new IllegalStateException("Нельзя удалить город, в котором есть дома. Сначала удалите или переместите дома.");
        }
        
        cityRepository.delete(city);
    }
}

