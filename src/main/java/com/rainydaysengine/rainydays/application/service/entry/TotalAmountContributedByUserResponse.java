package com.rainydaysengine.rainydays.application.service.entry;


import java.math.BigDecimal;

public record TotalAmountContributedByUserResponse(
    String groupName,
    Long combinedGoal,
    String firstName,
    String middleName,
    String lastName,
    String suffix,
    String profileUrl,
    BigDecimal total
) {}