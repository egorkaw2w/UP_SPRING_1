package com.example.demo.controller;

import com.example.demo.model.House;
import com.example.demo.model.Person;
import com.example.demo.service.CityService;
import com.example.demo.service.HouseService;
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
@RequestMapping("/houses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class HouseController {

    private final HouseService houseService;
    private final CityService cityService;
    private final PersonService personService;

    @GetMapping
    public String listHouses(
            @RequestParam(required = false) String addressFilter,
            @RequestParam(required = false) Double minSize,
            @RequestParam(required = false) String sortBy,
            Model model) {
        List<House> houses = houseService.getAll(addressFilter, minSize, sortBy);
        model.addAttribute("houses", houses);
        return "houses";
    }

    @GetMapping("/add")
    public String addHouseForm(Model model) {
        model.addAttribute("house", new House());
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("persons", personService.getAll(null, null, null));
        return "add-house";
    }

    @PostMapping("/add")
    public String addHouse(@Valid @ModelAttribute House house, 
                           BindingResult bindingResult,
                           @RequestParam(value = "city.id", required = false) Long cityId,
                           @RequestParam(value = "owner.id", required = false) Long ownerId,
                           Model model) {
        // Проверка обязательного поля city
        if (cityId == null || cityId == 0) {
            bindingResult.rejectValue("city", "error.city", "Город обязателен для заполнения");
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("cities", cityService.findAll());
            model.addAttribute("persons", personService.getAll(null, null, null));
            return "add-house";
        }
        
        if (cityId != null && cityId != 0) {
            house.setCity(cityService.findById(cityId));
        } else {
            bindingResult.rejectValue("city", "error.city", "Город обязателен для заполнения");
            model.addAttribute("cities", cityService.findAll());
            model.addAttribute("persons", personService.getAll(null, null, null));
            return "add-house";
        }
        
        if (ownerId != null && ownerId != 0) {
            Person owner = personService.getAll(null, null, null).stream()
                    .filter(p -> p.getId().equals(ownerId))
                    .findFirst()
                    .orElse(null);
            house.setOwner(owner);
        }
        
        try {
            houseService.save(house);
            return "redirect:/houses";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при сохранении: " + e.getMessage());
            model.addAttribute("cities", cityService.findAll());
            model.addAttribute("persons", personService.getAll(null, null, null));
            return "add-house";
        }
    }

    @GetMapping("/edit/{id}")
    public String editHouseForm(@PathVariable Long id, Model model) {
        House house = houseService.getAll(null, null, null).stream()
                .filter(h -> h.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Дом не найден"));
        model.addAttribute("house", house);
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("persons", personService.getAll(null, null, null));
        return "add-house";
    }

    @PostMapping("/update")
    public String updateHouse(@Valid @ModelAttribute House house, 
                              BindingResult bindingResult,
                              @RequestParam(value = "city.id", required = false) Long cityId,
                              @RequestParam(value = "owner.id", required = false) Long ownerId,
                              Model model) {
        // Проверка обязательного поля city
        if (cityId == null || cityId == 0) {
            bindingResult.rejectValue("city", "error.city", "Город обязателен для заполнения");
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("cities", cityService.findAll());
            model.addAttribute("persons", personService.getAll(null, null, null));
            return "add-house";
        }
        
        if (cityId != null && cityId != 0) {
            house.setCity(cityService.findById(cityId));
        }
        if (ownerId != null && ownerId != 0) {
            Person owner = personService.getAll(null, null, null).stream()
                    .filter(p -> p.getId().equals(ownerId))
                    .findFirst()
                    .orElse(null);
            house.setOwner(owner);
        } else {
            house.setOwner(null);
        }
        houseService.update(house);
        return "redirect:/houses";
    }

    // ФИЗИЧЕСКОЕ УДАЛЕНИЕ (ОДНА ЗАПИСЬ)
    @PostMapping("/delete/{id}")
    public String deleteHouse(@PathVariable Long id, RedirectAttributes ra) {
        try {
            houseService.delete(id);
            ra.addFlashAttribute("message", "Дом удален!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ошибка при удалении: " + e.getMessage());
        }
        return "redirect:/houses";
    }

    // ЛОГИЧЕСКОЕ УДАЛЕНИЕ (ОДНА ЗАПИСЬ)
    @PostMapping("/logical-delete/{id}")
    public String logicalDeleteHouse(@PathVariable Long id) {
        houseService.logicalDelete(id);
        return "redirect:/houses";
    }

    // ВОССТАНОВЛЕНИЕ (POST — как и все действия)
    @PostMapping("/restore/{id}")
    public String restoreHouse(@PathVariable Long id, RedirectAttributes ra) {
        try {
            houseService.restore(id);
            ra.addFlashAttribute("message", "Дом восстановлен!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/houses";
    }

    // МНОЖЕСТВЕННОЕ ЛОГИЧЕСКОЕ УДАЛЕНИЕ
    @PostMapping("/logical-delete-multiple")
    public String logicalDeleteMultiple(@RequestParam(required = false) List<Long> ids, RedirectAttributes ra) {
        if (ids == null || ids.isEmpty()) {
            ra.addFlashAttribute("error", "Выберите хотя бы одну запись!");
        } else {
            houseService.logicalDeleteAll(ids);
            ra.addFlashAttribute("message", "Логически удалено: " + ids.size());
        }
        return "redirect:/houses";
    }

    // МНОЖЕСТВЕННОЕ ФИЗИЧЕСКОЕ УДАЛЕНИЕ
    @PostMapping("/delete-multiple")
    public String deleteMultiple(@RequestParam(required = false) List<Long> ids, RedirectAttributes ra) {
        if (ids == null || ids.isEmpty()) {
            ra.addFlashAttribute("error", "Выберите хотя бы одну запись!");
        } else {
            try {
                houseService.deleteAll(ids);
                ra.addFlashAttribute("message", "Физически удалено: " + ids.size());
            } catch (Exception e) {
                ra.addFlashAttribute("error", "Ошибка при удалении: " + e.getMessage());
            }
        }
        return "redirect:/houses";
    }
}