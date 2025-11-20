package com.rainydaysengine.rainydays.infra.postgres.repository;

import com.rainydaysengine.rainydays.infra.postgres.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
1import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UsersEntity, UUID> {

    // JPQL only knows Entity Class name not table or column names
    @Query("SELECT u.id FROM UsersEntity u WHERE u.emailAddress = :emailAddress")
    Optional<String> findByEmailAddressString(@Param("emailAddress") String emailAddress);

    UsersEntity findByUsername(String username);

    UsersEntity findByEmailAddress(String emailAddress);

    @Modifying
    @Transactional
    @Query("UPDATE UsersEntity ue SET ue.password = :newPassword WHERE ue.id = :userId")
    void updatePassword(@Param("userId") UUID userId, @Param("newPassword") String newPassword);
}

