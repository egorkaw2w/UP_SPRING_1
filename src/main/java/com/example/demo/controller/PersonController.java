package com.example.demo.controller;

import com.example.demo.model.City;
import com.example.demo.model.Person;
import com.example.demo.service.CityService;
import com.example.demo.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/persons")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class PersonController {

    private final PersonService personService;
    private final CityService cityService;

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
        model.addAttribute("cities", cityService.findAll());
        return "add-person";
    }

    @PostMapping("/add")
    public String addPerson(@Valid @ModelAttribute Person person, 
                            BindingResult bindingResult, 
                            @RequestParam(value = "city.id", required = false) Long cityId,
                            Model model) {
        // Проверка обязательного поля city
        if (cityId == null || cityId == 0) {
            bindingResult.rejectValue("city", "error.city", "Город обязателен для заполнения");
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("cities", cityService.findAll());
            return "add-person";
        }
        
        // Устанавливаем город
        try {
            City city = cityService.findById(cityId);
            person.setCity(city);
            personService.save(person);
            return "redirect:/persons";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("city", "error.city", e.getMessage());
            model.addAttribute("cities", cityService.findAll());
            return "add-person";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при сохранении: " + e.getMessage());
            model.addAttribute("cities", cityService.findAll());
            return "add-person";
        }
    }

    @GetMapping("/edit/{id}")
    public String editPersonForm(@PathVariable Long id, Model model) {
        Person person = personService.getAll(null, null, null).stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Человек не найден"));
        model.addAttribute("person", person);
        model.addAttribute("cities", cityService.findAll());
        return "add-person";
    }

    @PostMapping("/update")
    public String updatePerson(@Valid @ModelAttribute Person person, 
                               BindingResult bindingResult,
                               @RequestParam(value = "city.id", required = false) Long cityId,
                               Model model) {
        // Проверка обязательного поля city
        if (cityId == null || cityId == 0) {
            bindingResult.rejectValue("city", "error.city", "Город обязателен для заполнения");
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("cities", cityService.findAll());
            return "add-person";
        }
        
        if (cityId != null && cityId != 0) {
            City city = cityService.findById(cityId);
            person.setCity(city);
        }
        personService.update(person);
        return "redirect:/persons";
    }

    @PostMapping("/delete/{id}")
    public String deletePerson(@PathVariable Long id, RedirectAttributes ra) {
        try {
            personService.delete(id);
            ra.addFlashAttribute("message", "Человек удален!");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ошибка при удалении: " + e.getMessage());
        }
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
            try {
                personService.deleteAll(ids);
                ra.addFlashAttribute("message", "Физически удалено: " + ids.size());
            } catch (IllegalStateException e) {
                ra.addFlashAttribute("error", e.getMessage());
            } catch (Exception e) {
                ra.addFlashAttribute("error", "Ошибка при удалении: " + e.getMessage());
            }
        }
        return "redirect:/persons";
    }
}