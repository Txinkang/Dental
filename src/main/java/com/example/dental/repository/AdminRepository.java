package com.example.dental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dental.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Admin findByAdminAccount(String adminAccount);
    
}
