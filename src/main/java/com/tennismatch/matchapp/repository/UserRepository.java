package com.tennismatch.matchapp.repository;

import com.tennismatch.matchapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    // We might need findByUsername if we re-introduce username, but email is primary for now
    // Optional<User> findByUsername(String username);
    // Boolean existsByUsername(String username);
} 