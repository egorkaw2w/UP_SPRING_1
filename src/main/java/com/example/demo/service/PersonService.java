package com.example.demo.service;

import com.example.demo.model.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {

    private final List<Person> persons = new ArrayList<>();
    private Long nextId = 1L;

    public List<Person> getAll(String nameFilter, Integer minAge, String sortBy) {
        var filtered = persons.stream()
                .filter(p -> nameFilter == null || p.getName().toLowerCase().contains(nameFilter.toLowerCase()))
                .filter(p -> minAge == null || p.getAge() >= minAge)
                .collect(Collectors.toList());

        if (sortBy != null) {
            switch (sortBy) {
                case "name" -> filtered.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                case "age" -> filtered.sort((a, b) -> Integer.compare(a.getAge(), b.getAge()));
                case "city" -> filtered.sort((a, b) -> a.getCity().compareToIgnoreCase(b.getCity()));
                case "gender" -> filtered.sort((a, b) -> a.getGender().compareToIgnoreCase(b.getGender()));
            }
        }
        return filtered;
    }

    public void save(Person person) {
        if (person.getId() == null) {
            person.setId(nextId++);
            persons.add(person);
        } else {
            update(person);
        }
    }

    public void update(Person person) {
        Person existing = persons.stream()
                .filter(p -> p.getId().equals(person.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Человек не найден"));
        existing.setName(person.getName());
        existing.setAge(person.getAge());
        existing.setCity(person.getCity());
        existing.setGender(person.getGender());
    }

    public void delete(Long id) {
        Person person = persons.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Человек не найден"));
        persons.remove(person);
    }

    public void logicalDelete(Long id) {
        Person person = persons.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Человек не найден"));
        person.setDeleted(true);
    }

    public void restore(Long id) {
        Person person = persons.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Человек не найден"));
        person.setDeleted(false);
    }

    public void logicalDeleteAll(List<Long> ids) {
        ids.forEach(this::logicalDelete);
    }

    public void deleteAll(List<Long> ids) {
        ids.forEach(this::delete);
    }
}