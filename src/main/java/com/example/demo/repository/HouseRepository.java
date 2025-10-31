package com.example.demo.repository;

import com.example.demo.model.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {
    @Query("SELECT h FROM House h WHERE h.deleted = false OR :includeDeleted = true")
    List<House> findAllByDeleted(@Param("includeDeleted") boolean includeDeleted);
    
    default List<House> findAllActive() {
        return findAllByDeleted(false);
    }
    
    @Modifying
    @Transactional
    @Query("UPDATE House h SET h.deleted = true WHERE h.id = :id")
    void logicalDeleteById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE House h SET h.deleted = false WHERE h.id = :id")
    void restoreById(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE House h SET h.deleted = true WHERE h.id IN :ids")
    void logicalDeleteMultiple(@Param("ids") List<Long> ids);
    
    @Query("SELECT COUNT(h) > 0 FROM House h WHERE h.owner.id = :personId")
    boolean existsHousesForPerson(@Param("personId") Long personId);
    
    @Query("SELECT COUNT(h) > 0 FROM House h WHERE h.city.id = :cityId")
    boolean existsHousesForCity(@Param("cityId") Long cityId);
}