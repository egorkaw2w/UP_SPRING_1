package com.example.demo.service;

import com.example.demo.model.Person;
import com.example.demo.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;

    public List<Person> getAll(String nameFilter, Integer minAge, String sortBy) {
        var filtered = personRepository.findAll().stream()
                .filter(p -> nameFilter == null || p.getName().toLowerCase().contains(nameFilter.toLowerCase()))
                .filter(p -> minAge == null || p.getAge() >= minAge)
                .collect(Collectors.toList());

        if (sortBy != null) {
            switch (sortBy) {
                case "name" -> filtered.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                case "age" -> filtered.sort((a, b) -> Integer.compare(a.getAge(), b.getAge()));
                case "city" -> filtered.sort((a, b) -> {
                    String cityA = a.getCity() != null ? a.getCity().getName() : "";
                    String cityB = b.getCity() != null ? b.getCity().getName() : "";
                    return cityA.compareToIgnoreCase(cityB);
                });
                case "gender" -> filtered.sort((a, b) -> a.getGender().compareToIgnoreCase(b.getGender()));
            }
        }
        return filtered;
    }

    @Transactional
    public Person save(Person person) {
        return personRepository.save(person);
    }

    @Transactional
    public Person update(Person person) {
        Person existing = personRepository.findById(person.getId())
                .orElseThrow(() -> new IllegalArgumentException("Человек не найден"));
        existing.setName(person.getName());
        existing.setAge(person.getAge());
        existing.setGender(person.getGender());
        if (person.getCity() != null) {
            existing.setCity(person.getCity());
        }
        return personRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) throws IllegalStateException {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Человек не найден"));
        
        // Проверка связей: нельзя удалить Person, если есть связанные House
        if (!person.getHouses().isEmpty()) {
            throw new IllegalStateException("Нельзя удалить человека, у которого есть дома. Сначала удалите или переназначьте дома.");
        }
        
        personRepository.delete(person);
    }

    @Transactional
    public void logicalDelete(Long id) {
        personRepository.logicalDeleteById(id);
    }

    @Transactional
    public void restore(Long id) {
        personRepository.restoreById(id);
    }

    @Transactional
    public void logicalDeleteAll(List<Long> ids) {
        personRepository.logicalDeleteMultiple(ids);
    }

    @Transactional
    public void deleteAll(List<Long> ids) throws IllegalStateException {
        for (Long id : ids) {
            delete(id);
        }
    }
}
