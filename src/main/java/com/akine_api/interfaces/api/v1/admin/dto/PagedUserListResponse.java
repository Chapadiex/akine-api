package com.akine_api.interfaces.api.v1.admin.dto;

import java.util.List;

public record PagedUserListResponse(
        List<UserSummaryResponse> content,
        int page,
        int size,
        long totalElements
) {}
