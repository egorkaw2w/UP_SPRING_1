package com.example.demo.repository;

import com.example.demo.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("SELECT p FROM Person p WHERE p.deleted = false OR :includeDeleted = true")
    List<Person> findAllByDeleted(@Param("includeDeleted") boolean includeDeleted);
    
    default List<Person> findAllActive() {
        return findAllByDeleted(false);
    }
    
    @Modifying
    @Transactional
    @Query("UPDATE Person p SET p.deleted = true WHERE p.id = :id")
    void logicalDeleteById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE Person p SET p.deleted = false WHERE p.id = :id")
    void restoreById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE Person p SET p.deleted = true WHERE p.id IN :ids")
    void logicalDeleteMultiple(@Param("ids") List<Long> ids);
    
    @Query("SELECT COUNT(p) > 0 FROM Person p WHERE p.city.id = :cityId")
    boolean existsPersonsForCity(@Param("cityId") Long cityId);
}