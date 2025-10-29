package com.example.demo.controller;

import com.example.demo.model.House;
import com.example.demo.service.HouseService;
import com.example.demo.service.CityService;
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
    private final CityService cityService;

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
        model.addAttribute("cities", cityService.getAll());
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
    public String deleteHouse(@PathVariable Long id, RedirectAttributes ra) {
        try {
            houseService.delete(id);
            ra.addFlashAttribute("message", "Дом удалён");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
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
            int ok = 0; int fail = 0;
            for (Long id : ids) {
                try { houseService.delete(id); ok++; } catch (Exception e) { fail++; }
            }
            if (ok > 0) ra.addFlashAttribute("message", "Удалено: " + ok);
            if (fail > 0) ra.addFlashAttribute("error", "Не удалено из-за связей: " + fail);
        }
        return "redirect:/houses";
    }
}