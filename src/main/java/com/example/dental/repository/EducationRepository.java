package com.example.dental.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.dental.model.Education;

public interface EducationRepository extends JpaRepository<Education, String> {

    Page<Education> findByEducationTitle(String educationTitle, Pageable pageable);

    Education findByEducationId(String educationId);
    
}
