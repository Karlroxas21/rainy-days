package com.rainydaysengine.rainydays.infra.postgres.repository;

import com.rainydaysengine.rainydays.application.service.usersgroup.UserGroupsResponse;
import com.rainydaysengine.rainydays.infra.postgres.entity.usersgroup.UsersGroupEntity;
import com.rainydaysengine.rainydays.infra.postgres.entity.usersgroup.UsersGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UsersGroupRepository extends JpaRepository<UsersGroupEntity, UsersGroupId> {
    @Query("""
            SELECT 
                g.id,
                g.groupName,
                g.combinedGoal
            FROM 
                GroupEntity g
                LEFT JOIN UsersGroupEntity ug ON g.id = ug.groupId
            WHERE 
                ug.userId = :userId
            AND
                g.deletedAt IS NULL
            """)
    List<UserGroupsResponse> findAllUserGroups(@Param("userId") UUID userId);
}
