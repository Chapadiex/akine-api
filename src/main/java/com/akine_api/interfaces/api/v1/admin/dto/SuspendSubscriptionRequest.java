package com.akine_api.interfaces.api.v1.admin.dto;

import jakarta.validation.constraints.Size;

public record SuspendSubscriptionRequest(
        @Size(max = 500) String reason
) {}
