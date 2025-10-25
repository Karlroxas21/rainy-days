package com.rainydaysengine.rainydays.infra.postgres.repository;

import com.rainydaysengine.rainydays.application.service.entry.EntryResponse;
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
//     SELECT 
// 	g.group_name,
// 	g.combined_goal,
// 	u.first_name,
// 	u.middle_name,
// 	u.last_name,
// 	u.suffix,
// 	u.profile_url,
// 	COALESCE(SUM(e.amount), 0) AS total
// FROM user_entries ue 
// LEFT JOIN entries e ON ue.entry_id = e.id
// LEFT JOIN users u ON  ue.user_id = u.id
// LEFT JOIN groups g ON ue.group_id = g.id
// where 
// 	ue.user_id='9f5ffc14-bbf8-4248-914b-990ea4c3315b'
// and
// 	group_id='1c95ea3b-d803-4425-b1b2-b4c6f2a0dd91'
// GROUP BY 
//     g.group_name,  -- You must group by all the other columns you are selecting
//     g.combined_goal,
//     u.first_name,
//     u.middle_name,
//     u.last_name,
//     u.suffix,
//     u.profile_url;

}