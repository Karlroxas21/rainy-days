package com.rainydaysengine.rainydays.infra.postgres.repository;

import com.rainydaysengine.rainydays.infra.postgres.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UsersEntity, UUID> {

    // JPQL only knows Entity Class name not table or column names
    @Query("SELECT u.iamId FROM UsersEntity u WHERE u.emailAddress = :emailAddress")
    Optional<String> findByEmailAddress(@Param("emailAddress") String emailAddress);

    Optional<UsersEntity> findByUsername(String username);
}
