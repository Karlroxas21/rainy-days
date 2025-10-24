package com.rainydaysengine.rainydays.infra.postgres.repository;

import com.rainydaysengine.rainydays.infra.postgres.entity.usersgroup.UsersGroupEntity;
import com.rainydaysengine.rainydays.infra.postgres.entity.usersgroup.UsersGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersGroupRepository extends JpaRepository<UsersGroupEntity, UsersGroupId> {
}
