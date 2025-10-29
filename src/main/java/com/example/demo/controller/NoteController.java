package com.example.demo.controller;

import com.example.demo.model.Note;
import com.example.demo.repository.PersonRepository;
import com.example.demo.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;
    private final PersonRepository personRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("notes", noteService.getAll());
        return "notes";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("note", new Note());
        model.addAttribute("persons", personRepository.findAll());
        return "add-note";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute Note note, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("persons", personRepository.findAll());
            return "add-note";
        }
        noteService.save(note);
        return "redirect:/notes";
    }
}
