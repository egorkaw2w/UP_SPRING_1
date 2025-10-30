package com.example.demo.controller;

import com.example.demo.model.Person;
import com.example.demo.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public String listPersons(
            @RequestParam(required = false) String nameFilter,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) String sortBy,
            Model model) {
        List<Person> persons = personService.getAll(nameFilter, minAge, sortBy);
        model.addAttribute("persons", persons);
        return "persons";
    }

    @GetMapping("/add")
    public String addPersonForm(Model model) {
        model.addAttribute("person", new Person());
        return "add-person";
    }

    @PostMapping("/add")
    public String addPerson(@Valid @ModelAttribute Person person, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "add-person";
        }
        personService.save(person);
        return "redirect:/persons";
    }

    @PostMapping("/update")
    public String updatePerson(@Valid @ModelAttribute Person person, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "add-person";
        }
        personService.update(person);
        return "redirect:/persons";
    }

    @PostMapping("/delete/{id}")
    public String deletePerson(@PathVariable Long id) {
        personService.delete(id);
        return "redirect:/persons";
    }

    @PostMapping("/logical-delete/{id}")
    public String logicalDeletePerson(@PathVariable Long id) {
        personService.logicalDelete(id);
        return "redirect:/persons";
    }

    @PostMapping("/restore/{id}")
    public String restorePerson(@PathVariable Long id, RedirectAttributes ra) {
        try {
            personService.restore(id);
            ra.addFlashAttribute("message", "Человек восстановлен!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/persons";
    }

    @PostMapping("/logical-delete-multiple")
    public String logicalDeleteMultiple(@RequestParam(required = false) List<Long> ids, RedirectAttributes ra) {
        if (ids == null || ids.isEmpty()) {
            ra.addFlashAttribute("error", "Выберите хотя бы одну запись!");
        } else {
            personService.logicalDeleteAll(ids);
            ra.addFlashAttribute("message", "Логически удалено: " + ids.size());
        }
        return "redirect:/persons";
    }

    @PostMapping("/delete-multiple")
    public String deleteMultiple(@RequestParam(required = false) List<Long> ids, RedirectAttributes ra) {
        if (ids == null || ids.isEmpty()) {
            ra.addFlashAttribute("error", "Выберите хотя бы одну запись!");
        } else {
            personService.deleteAll(ids);
            ra.addFlashAttribute("message", "Физически удалено: " + ids.size());
        }
        return "redirect:/persons";
    }
}