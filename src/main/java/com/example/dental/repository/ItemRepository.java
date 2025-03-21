package com.example.dental.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dental.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByItemName(String itemName, Pageable pageable);

    Item findByItemId(String itemId);

    Item findByItemName(String itemName);
    
}
