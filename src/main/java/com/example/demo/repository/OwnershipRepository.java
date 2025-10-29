package com.example.demo.repository;

import com.example.demo.model.Ownership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnershipRepository extends JpaRepository<Ownership, Long> {
    long countByPersonId(Long personId);
    long countByHouseId(Long houseId);
}


