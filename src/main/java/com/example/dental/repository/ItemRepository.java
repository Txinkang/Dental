package com.example.dental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dental.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
    
}
