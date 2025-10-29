package com.example.demo.controller;

import com.example.demo.model.Ownership;
import com.example.demo.repository.HouseRepository;
import com.example.demo.repository.PersonRepository;
import com.example.demo.service.OwnershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ownerships")
@RequiredArgsConstructor
public class OwnershipController {
    private final OwnershipService ownershipService;
    private final PersonRepository personRepository;
    private final HouseRepository houseRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("ownerships", ownershipService.getAll());
        return "ownerships";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("ownership", new Ownership());
        model.addAttribute("persons", personRepository.findAll());
        model.addAttribute("houses", houseRepository.findAll());
        return "add-ownership";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute Ownership ownership, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("persons", personRepository.findAll());
            model.addAttribute("houses", houseRepository.findAll());
            return "add-ownership";
        }
        ownershipService.save(ownership);
        return "redirect:/ownerships";
    }
}


