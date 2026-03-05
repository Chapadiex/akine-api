package com.akine_api.interfaces.api.v1.admin.dto;

import java.util.List;

public record SubscriptionDetailResponse(
        SubscriptionSummaryResponse subscription,
        List<SubscriptionAuditItemResponse> auditTrail
) {}
