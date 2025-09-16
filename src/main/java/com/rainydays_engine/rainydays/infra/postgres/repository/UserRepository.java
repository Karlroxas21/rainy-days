package com.rainydays_engine.rainydays.infra.postgres.repository;

import com.rainydays_engine.rainydays.infra.postgres.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {

    // JPQL only knows Entity Class name not table or column names
    @Query("SELECT u.iamId FROM Users u WHERE u.emailAddress = :emailAddress")
    Optional<String> findByEmailAddress(@Param("emailAddress") String emailAddress);

    Optional<Users> findByUsername(String username);
}
