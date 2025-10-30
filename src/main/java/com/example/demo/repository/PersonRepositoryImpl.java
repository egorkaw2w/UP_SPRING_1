package com.example.demo.repository;

import com.example.demo.model.Person;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class PersonRepositoryImpl implements PersonRepository {
    private final List<Person> persons = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Person> findAll(boolean includeDeleted) {
        return persons.stream()
                .filter(p -> includeDeleted || !p.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Person> findById(Long id) {
        return persons.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    @Override
    public Person save(Person person) {
        if (person.getId() == null) {
            person.setId(idGenerator.getAndIncrement());
            persons.add(person);
            return person;
        } else {
            return findById(person.getId()).map(existing -> {
                existing.setName(person.getName());
                existing.setAge(person.getAge());
                existing.setCity(person.getCity());
                existing.setGender(person.getGender());
                return existing;
            }).orElseGet(() -> {
                persons.add(person);
                return person;
            });
        }
    }

    @Override
    public void deleteById(Long id) {
        persons.removeIf(p -> p.getId().equals(id));
    }

    @Override
    public void logicalDeleteById(Long id) {
        findById(id).ifPresent(p -> p.setDeleted(true));
    }

    @Override
    public void deleteMultiple(List<Long> ids) {
        persons.removeIf(p -> ids.contains(p.getId()));
    }

    @Override
    public void logicalDeleteMultiple(List<Long> ids) {
        persons.stream()
                .filter(p -> ids.contains(p.getId()))
                .forEach(p -> p.setDeleted(true));
    }
}