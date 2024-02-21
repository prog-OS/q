package com.example.project.pharmacy.repository;

import com.example.project.pharmacy.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;

// JPA CRUD 사용 가능
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> { // JpaRepository<entity, PK타입>
}
