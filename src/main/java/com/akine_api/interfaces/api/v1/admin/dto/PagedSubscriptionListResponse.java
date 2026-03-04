package com.akine_api.interfaces.api.v1.admin.dto;

import java.util.List;

public record PagedSubscriptionListResponse(
        List<SubscriptionSummaryResponse> content,
        int page,
        int size,
        long totalElements
) {}
