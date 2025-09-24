package com.rainydaysengine.rainydays.infra.postgres.repository;

import com.rainydaysengine.rainydays.infra.postgres.entity.UserEntriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserEntriesRepository extends JpaRepository<UserEntriesEntity, UUID> {
}
