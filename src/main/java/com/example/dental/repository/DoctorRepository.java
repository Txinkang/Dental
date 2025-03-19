package com.example.dental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dental.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
}
