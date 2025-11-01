package com.rainydaysengine.rainydays.infra.postgres.repository;

import com.rainydaysengine.rainydays.infra.postgres.entity.entries.EntriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EntryRepository extends JpaRepository<EntriesEntity, UUID> {
    Optional<EntriesEntity> findByUserId(UUID userId);
}
