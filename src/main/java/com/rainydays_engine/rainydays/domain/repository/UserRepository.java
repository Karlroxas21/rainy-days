package com.rainydays_engine.rainydays.domain.repository;

import com.rainydays_engine.rainydays.infra.postgres.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByEmailAddress(String emailAddress);
    Optional<Users> findByUsername(String username);
}
