package com.example.demo.controller;

import com.example.demo.model.City;
import com.example.demo.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("cities", cityService.getAll());
        return "cities";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("city", new City());
        return "add-city";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute City city, BindingResult br) {
        if (br.hasErrors()) return "add-city";
        cityService.save(city);
        return "redirect:/cities";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            cityService.delete(id);
            ra.addFlashAttribute("message", "Город удалён");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cities";
    }
}


