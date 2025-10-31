package com.rainydaysengine.rainydays.infra.postgres.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.rainydaysengine.rainydays.application.service.entry.groupstatistics.GroupProgress;
import com.rainydaysengine.rainydays.application.service.entry.groupstatistics.MemberRanking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rainydaysengine.rainydays.application.service.entry.EntryResponse;
import com.rainydaysengine.rainydays.application.service.entry.RecentEntriesResponse;
import com.rainydaysengine.rainydays.application.service.entry.TotalAmountContributedByUserResponse;
import com.rainydaysengine.rainydays.infra.postgres.entity.UserEntriesEntity;

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

    // Select total amount of contributed by user in group
    @NativeQuery("""
            SELECT
                g.group_name,
                g.combined_goal,
                u.first_name,
                u.middle_name,
                u.last_name,
                u.suffix,
                u.profile_url,
                SUM(e.amount) AS total
            FROM
                user_entries ue
            LEFT JOIN
                entries e ON ue.entry_id = e.id
            LEFT JOIN
                users u ON ue.user_id = u.id
            LEFT JOIN
                groups g ON ue.group_id = g.id
            WHERE
                ue.user_id = :userId
            AND
                ue.group_id = :groupId
            GROUP BY
                g.group_name,
                g.combined_goal,
                u.first_name,
                u.middle_name,
                u.last_name,
                u.suffix,
                u.profile_url
           """)
    Optional<TotalAmountContributedByUserResponse> findTotalAmountContributedByUser(@Param("userId") UUID userId, @Param("groupId") UUID groupId);

    // Select Group Progress
    @NativeQuery("""
           SELECT
                g.group_name,
                g.combined_goal,
                SUM(e.amount) as total
           FROM
                user_entries ue
           LEFT JOIN
                entries e on ue.entry_id = e.id
           LEFT JOIN
                groups g on ue.group_id = g.id
           WHERE 
                ue.group_id = :groupId
           GROUP BY
                g.group_name,
                g.combined_goal
           """)
    Optional<GroupProgress> getGroupProgress(@Param("groupId") UUID groupId);

//    Group Total in current month
    @NativeQuery("""
            SELECT
                g.group_name,
                g.combined_goal,
                SUM(e.amount) AS total
            FROM
                user_entries ue
            LEFT JOIN
                entries e ON ue.entry_id = e.id
            LEFT JOIN
                users u ON ue.user_id = u.id
            LEFT JOIN
                groups g ON ue.group_id = g.id
            WHERE
                ue.group_id = :groupId
            AND
                ue.created_at >= DATE_TRUNC('month', CURRENT_DATE)
            AND
                ue.created_at < DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month'
            GROUP BY
                g.group_name,
                g.combined_goal
            """)
    Optional<GroupProgress> getGroupTotalInCurrentMonth(@Param("groupId") UUID groupId);

//    This month most_active or member_rankings(just get the first index)
    @NativeQuery("""
            SELECT
                ue.user_id,
                SUM(e.amount) as totalContribution
            FROM
                user_entries ue
            LEFT JOIN
                entries e on ue.entry_id = e.id
            WHERE
                ue.group_id = :groupId
            GROUP BY
                ue.user_id
            """)
    List<MemberRanking> getMemberRankingInCurrentMonth(@Param("groupId") UUID groupId);

}