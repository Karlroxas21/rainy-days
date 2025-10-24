package com.rainydaysengine.rainydays.infra.postgres.entity.usersgroup;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users_group")
@IdClass(UsersGroupId.class) // JPA to use this class for the ID
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersGroupEntity {

    @Id()
    @Column(name = "user_id")
    private UUID userId;

    @Id()
    @Column(name = "group_id")
    private UUID groupId;

    @Column(name = "created_at", updatable = false, insertable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
