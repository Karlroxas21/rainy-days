package com.rainydaysengine.rainydays.domain.service.group;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupDto {

    @NotBlank(message = "Group Name cannot be empty")
    private String groupName;

    @NotNull(message = "Combined Goal cannot be empty")
    @Max(value = 1000000, message = "Combined Goal must not exceed 1,000,000")
    @Min(value = 1, message = "Combined Goal must be at least 1")
    private int combinedGoal;
}
