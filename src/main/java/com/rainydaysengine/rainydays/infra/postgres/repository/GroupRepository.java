package com.rainydaysengine.rainydays.infra.postgres.repository;

import com.rainydaysengine.rainydays.infra.postgres.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {

    @Query("SELECT g.groupName FROM GroupEntity g WHERE g.groupName = :groupName")
    Optional<String> findByGroupName(@Param("groupName") String groupName);

    @Query("SELECT COUNT(*) as totalMembers FROM UsersGroupEntity WHERE groupId = :groupId")
    int findTotalGroupMembers(@Param("groupId") UUID groupId);
}
