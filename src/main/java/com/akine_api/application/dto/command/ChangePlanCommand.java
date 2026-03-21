package com.akine_api.application.dto.command;

import java.util.UUID;

public record ChangePlanCommand(UUID subscriptionId, String newPlanCode, String callerEmail, boolean isAdmin) {}
