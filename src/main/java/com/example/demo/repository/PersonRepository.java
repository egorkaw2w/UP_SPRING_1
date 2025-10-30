package com.example.demo.repository;

import com.example.demo.model.Person;
import java.util.List;
import java.util.Optional;

public interface PersonRepository {
    List<Person> findAll(boolean includeDeleted);
    default List<Person> findAll() {
        return findAll(false);
    }
    Optional<Person> findById(Long id);
    Person save(Person person);
    void deleteById(Long id);
    default void delete(Person person) {
        if (person != null && person.getId() != null) {
            deleteById(person.getId());
        }
    }
    void logicalDeleteById(Long id);
    void deleteMultiple(List<Long> ids);
    void logicalDeleteMultiple(List<Long> ids);
}