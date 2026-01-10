package com.rainydaysengine.rainydays.infra.postgres.repository;

import com.rainydaysengine.rainydays.application.service.entry.EntryResponse;
import com.rainydaysengine.rainydays.application.service.entry.RecentEntriesResponse;
import com.rainydaysengine.rainydays.application.service.entry.TotalAmountContributedByUserResponse;
import com.rainydaysengine.rainydays.application.service.entry.TotalPersonalFundByUserResponse;
import com.rainydaysengine.rainydays.application.service.entry.groupstatistics.GroupProgress;
import com.rainydaysengine.rainydays.application.service.entry.groupstatistics.MemberRanking;
import com.rainydaysengine.rainydays.application.service.entry.history.AllRecentEntriesInGroup;
import com.rainydaysengine.rainydays.application.service.entry.history.EntriesSummaryHistory;
import com.rainydaysengine.rainydays.infra.postgres.entity.UserEntriesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserEntriesRepository extends JpaRepository<UserEntriesEntity, UUID> {
    Optional<UserEntriesEntity> findByUserId(UUID uuid);

    @Query("""
             SELECT
                 e.id,
                 e.entryType,
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
             AND
                 g.id = :groupId
                 AND (
                       LOWER(e.notes) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(g.groupName) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR CAST(e.amount AS text) LIKE CONCAT('%', :search, '%')
                  )
            """)
    Page<RecentEntriesResponse> findAllRecentEntriesByUserIdAndGroupId(@Param("userId") UUID userId, @Param("search") String search, Pageable pageable);

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


    @NativeQuery(
            """
            SELECT SUM(e.amount) AS total_personal_fund
            FROM
                user_entries ue
            LEFT JOIN
                entries e ON ue.entry_id = e.id
            WHERE 
                ue.user_id = :userId
            AND
                group_id IS NULL 
            """
    )
    TotalPersonalFundByUserResponse findTotalPersonalFundByUser(@Param("userId") UUID userId);

    /**
     * SELECT SUM(e.amount) as total_personal_fund FROM user_entries ue
     * LEFT JOIN entries e on ue.entry_id = e.id
     * where group_id IS NULL
     *
     * @param groupId
     * @return
     */

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

    // Group Total in current month
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

    // This month most_active or member_rankings(just get the first index)
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

    @NativeQuery("""
            SELECT
                SUM(e.amount) as total,
                e.entry_type
            FROM
                user_entries ue
            JOIN
                entries e on ue.entry_id = e.id
            WHERE
                group_id = :groupId
            AND
                e.entry_type = :entryType
            GROUP BY
                e.entry_type
            """)
    Optional<EntriesSummaryHistory> getEntriesSummaryHistory(@Param("groupId") UUID groupId, @Param("entryType") String entryType);

    @Query("""
            SELECT
                e.userId,
                g.id as groupId,
                e.amount,
                e.entryType,
                e.notes,
                e.photoEvidence,
                ue.createdAt
            FROM
                UserEntriesEntity ue
            LEFT JOIN
                GroupEntity g ON ue.groupId = g.id
            LEFT JOIN
                EntriesEntity e ON ue.entryId = e.id
            WHERE
                ue.groupId = :groupId
            AND
                (:month IS NULL OR EXTRACT(MONTH FROM ue.createdAt) = :month)
            AND
                (:year IS NULL OR EXTRACT(YEAR FROM ue.createdAt) = :year)
            """)
    Page<AllRecentEntriesInGroup> getAllEntriesInGroup(
            @Param("groupId") UUID groupId,
            @RequestParam("month") Integer month,
            @RequestParam("year") Integer year,
            Pageable pageable);
}