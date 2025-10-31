package com.example.demo.controller;

import com.example.demo.model.City;
import com.example.demo.service.CityService;
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
@RequestMapping("/cities")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class CityController {
    private final CityService cityService;

    @GetMapping
    public String listCities(Model model) {
        List<City> cities = cityService.findAll();
        model.addAttribute("cities", cities);
        return "cities";
    }

    @GetMapping("/add")
    public String addCityForm(Model model) {
        model.addAttribute("city", new City());
        return "add-city";
    }

    @PostMapping("/add")
    public String addCity(@Valid @ModelAttribute City city, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "add-city";
        }
        try {
            cityService.save(city);
            ra.addFlashAttribute("message", "Город добавлен!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ошибка при добавлении: " + e.getMessage());
        }
        return "redirect:/cities";
    }

    @GetMapping("/edit/{id}")
    public String editCityForm(@PathVariable Long id, Model model) {
        City city = cityService.findById(id);
        model.addAttribute("city", city);
        return "add-city";
    }

    @PostMapping("/update")
    public String updateCity(@Valid @ModelAttribute City city, BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "add-city";
        }
        try {
            cityService.update(city);
            ra.addFlashAttribute("message", "Город обновлен!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ошибка при обновлении: " + e.getMessage());
        }
        return "redirect:/cities";
    }

    @PostMapping("/delete/{id}")
    public String deleteCity(@PathVariable Long id, RedirectAttributes ra) {
        try {
            cityService.delete(id);
            ra.addFlashAttribute("message", "Город удален!");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ошибка при удалении: " + e.getMessage());
        }
        return "redirect:/cities";
    }
}

