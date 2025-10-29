package com.example.demo.service;

import com.example.demo.model.Ownership;
import com.example.demo.repository.OwnershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnershipService {
    private final OwnershipRepository ownershipRepository;

    public List<Ownership> getAll() { return ownershipRepository.findAll(); }

    public Ownership save(Ownership ownership) { return ownershipRepository.save(ownership); }
}


