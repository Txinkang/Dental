package com.example.dental.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dental.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, String> {

    Page<Doctor> findByDoctorName(String doctorName, Pageable pageable);

    Doctor findByDoctorId(String doctorId);

    Doctor findByDoctorName(String doctorName);
}
