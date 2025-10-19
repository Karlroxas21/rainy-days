package com.rainydaysengine.rainydays.infra.postgres.repository;

import com.rainydaysengine.rainydays.application.service.entry.RecentEntriesResponse;
import com.rainydaysengine.rainydays.infra.postgres.entity.UserEntriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserEntriesRepository extends JpaRepository<UserEntriesEntity, UUID> {
    Optional<UserEntriesEntity> findByUserId(UUID uuid);

    @Query("""
            SELECT
                e.id,
                e.amount,
                e.notes,
                e.photoEvidence,
                g.id,
                g.groupName
            FROM
                UserEntriesEntity ue
                JOIN EntriesEntity e ON ue.entryId = e.id
                JOIN GroupEntity g ON ue.groupId = g.id
            WHERE 
                ue.userId = :userId
                AND (
                      LOWER(e.notes) LIKE LOWER(CONCAT('%', :search, '%'))
                      OR LOWER(g.groupName) LIKE LOWER(CONCAT('%', :search, '%'))
                      OR CAST(e.amount AS text) LIKE CONCAT('%', :search, '%')
                 )
           """)
    Page<RecentEntriesResponse> findAllRecentEntriesByUserId(@Param("userId") UUID userId, @Param("search") String search, Pageable pageable);
}