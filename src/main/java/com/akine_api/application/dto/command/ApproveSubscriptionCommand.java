package com.akine_api.application.dto.command;

import java.time.LocalDate;
import java.util.UUID;

public record ApproveSubscriptionCommand(
        UUID subscriptionId,
        UUID actorUserId,
        LocalDate startDate,
        LocalDate endDate
) {}
