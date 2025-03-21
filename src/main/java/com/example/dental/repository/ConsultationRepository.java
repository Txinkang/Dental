package com.example.dental.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dental.model.Consultation;

public interface ConsultationRepository extends JpaRepository<Consultation, String> {

    List<Consultation> findByUserId(String userId);

    Consultation findByConsultationId(String consultationId);
    
}
