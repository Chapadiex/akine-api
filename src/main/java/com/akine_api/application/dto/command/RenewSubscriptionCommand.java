package com.akine_api.application.dto.command;

import java.util.UUID;

public record RenewSubscriptionCommand(UUID subscriptionId, String callerEmail, boolean isAdmin) {}
