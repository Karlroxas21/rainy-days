package com.rainydaysengine.rainydays.infra.postgres.repository;

import com.rainydaysengine.rainydays.application.service.entry.EntryResponse;
import com.rainydaysengine.rainydays.application.service.entry.RecentEntriesResponse;
import com.rainydaysengine.rainydays.application.service.entry.TotalAmountContributedByUserResponse;
import com.rainydaysengine.rainydays.infra.postgres.entity.UserEntriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("checkstyle:MissingJavadocType")
public interface UserEntriesRepository extends JpaRepository<UserEntriesEntity, UUID> {
    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    Optional<UserEntriesEntity> findByUserId(UUID uuid);

    @SuppressWarnings({"checkstyle:LineLength", "checkstyle:MissingJavadocMethod"})
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

    @Query("""
           SELECT
                e.id,
                e.amount,
                e.notes,
                e.photoEvidence,
                e.createdAt,
                e.updatedAt,
                g.id,
                g.groupName
           FROM
                UserEntriesEntity ue
                JOIN EntriesEntity e ON ue.entryId = e.id
                JOIN GroupEntity g ON ue.groupId = g.id
           WHERE
                ue.entryId = :entryId
           AND 
                ue.userId = :userId
           """)
    Optional<EntryResponse> findEntryById(@Param("entryId") UUID entryId, @Param("userId") UUID userId);


    // Select and total amount of contributed by user in group
    @Query("""
           SELECT
                g.groupName,
                g.combinedGoal,
                u.firstName,
                u.middleName,
                u.lastName,
                u.suffix,
                u.profileUrl,
                COALESCE(SUM(e.amount), 0) AS total
           FROM
                UserEntriesEntity ue
           LEFT JOIN
                EntriesEntity e ON ue.entryId = e.id
           LEFT JOIN
                UsersEntity u ON ue.userId = u.id
           LEFT JOIN
                GroupEntity g ON ue.groupId = g.id
           WHERE
                ue.userId = :userId
           AND
                ue.groupId = :groupId
           GROUP BY
                g.groupName,
                u.firstName,
                u.middleName,
                u.lastName,
                u.suffix,
                u.profileUrl
           """)
    TotalAmountContributedByUserResponse findTotalAmountContributedByUser(@Param("userId") UUID userId, @Param("groupId") UUID groupId);
}