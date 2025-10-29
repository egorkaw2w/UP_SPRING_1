package com.example.demo.service;

import com.example.demo.model.City;
import com.example.demo.model.Person;
import com.example.demo.repository.NoteRepository;
import com.example.demo.repository.OwnershipRepository;
import com.example.demo.repository.PersonRepository;
import com.example.demo.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final NoteRepository noteRepository;
    private final OwnershipRepository ownershipRepository;
    private final CityRepository cityRepository;

    public List<Person> getAll(String nameFilter, Integer minAge, String sortBy) {
        Specification<Person> spec = (root, q, cb) -> cb.conjunction();
        if (nameFilter != null && !nameFilter.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("name")), "%" + nameFilter.toLowerCase() + "%"));
        }
        if (minAge != null) {
            spec = spec.and((root, q, cb) -> cb.ge(root.get("age"), minAge));
        }

        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isBlank()) {
            sort = switch (sortBy) {
                case "name" -> Sort.by("name");
                case "age" -> Sort.by("age");
                case "city" -> Sort.by("city.name");
                case "gender" -> Sort.by("gender");
                default -> Sort.unsorted();
            };
        }
        return personRepository.findAll(spec, sort);
    }

    public void save(Person person) {
        if (person.getCityName() != null && !person.getCityName().isBlank()) {
            City city = cityRepository.findAll().stream()
                    .filter(c -> c.getName().equalsIgnoreCase(person.getCityName()))
                    .findFirst()
                    .orElseGet(() -> cityRepository.save(City.builder().name(person.getCityName()).build()));
            person.setCity(city);
        }
        personRepository.save(person);
    }

    public void update(Person person) {
        save(person);
    }

    public void delete(Long id) {
        // Integrity check: no notes or ownerships
        if (noteRepository.countByAuthorId(id) > 0) {
            throw new IllegalStateException("Нельзя удалить: у человека есть заметки");
        }
        if (ownershipRepository.countByPersonId(id) > 0) {
            throw new IllegalStateException("Нельзя удалить: у человека есть дома");
        }
        personRepository.deleteById(id);
    }

    public void logicalDelete(Long id) {
        Person p = personRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Человек не найден"));
        p.setDeleted(true);
        personRepository.save(p);
    }

    public void restore(Long id) {
        Person p = personRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Человек не найден"));
        p.setDeleted(false);
        personRepository.save(p);
    }

    public void logicalDeleteAll(List<Long> ids) {
        ids.forEach(this::logicalDelete);
    }

    public void deleteAll(List<Long> ids) {
        ids.forEach(this::delete);
    }
}