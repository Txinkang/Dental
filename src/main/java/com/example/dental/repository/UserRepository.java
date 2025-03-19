package com.example.dental.repository;

import com.example.dental.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUserAccount(String userAccount);

    User findByUserId(String userId);
}
