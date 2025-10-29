package com.example.demo.service;

import com.example.demo.model.City;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepository;
    private final PersonRepository personRepository;

    public List<City> getAll() { return cityRepository.findAll(); }

    public City save(City city) { return cityRepository.save(city); }

    public void delete(Long id) {
        if (personRepository.countByCityId(id) > 0) {
            throw new IllegalStateException("Нельзя удалить город: есть жители");
        }
        cityRepository.deleteById(id);
    }
}


