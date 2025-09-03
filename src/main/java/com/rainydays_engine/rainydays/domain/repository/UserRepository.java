package com.rainydays_engine.rainydays.domain.repository;

import com.rainydays_engine.rainydays.infra.postgres.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {
}
