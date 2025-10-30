package com.example.demo.controller.api;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Person;
import com.example.demo.repository.PersonRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonRestController {
    private final PersonRepository personRepository;

    @GetMapping
    public List<Person> list() {
        return personRepository.findAll();
    }

    @GetMapping("/{id}")
    public Person get(@PathVariable Long id) {
        return personRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Человек не найден: " + id));
    }

    @PostMapping
    public ResponseEntity<Person> create(@Valid @RequestBody Person p) {
        Person saved = personRepository.save(p);
        return ResponseEntity.created(URI.create("/api/persons/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public Person update(@PathVariable Long id, @Valid @RequestBody Person p) {
        Person existing = personRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Человек не найден: " + id));
        existing.setName(p.getName());
        existing.setAge(p.getAge());
        existing.setCity(p.getCity());
        existing.setGender(p.getGender());
        return personRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Person p = personRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Человек не найден: " + id));
        personRepository.delete(p);
    }
}

