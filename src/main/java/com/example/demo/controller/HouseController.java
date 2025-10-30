package com.example.demo.controller;

import com.example.demo.model.House;
import com.example.demo.service.HouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/houses")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;

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
        return "add-house";
    }

    @PostMapping("/add")
    public String addHouse(@Valid @ModelAttribute House house, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "add-house";
        }
        houseService.save(house);
        return "redirect:/houses";
    }

    @PostMapping("/update")
    public String updateHouse(@Valid @ModelAttribute House house, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "add-house";
        }
        houseService.update(house);
        return "redirect:/houses";
    }

    // ФИЗИЧЕСКОЕ УДАЛЕНИЕ (ОДНА ЗАПИСЬ)
    @PostMapping("/delete/{id}")
    public String deleteHouse(@PathVariable Long id) {
        houseService.delete(id);
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
            houseService.deleteAll(ids);
            ra.addFlashAttribute("message", "Физически удалено: " + ids.size());
        }
        return "redirect:/houses";
    }
}