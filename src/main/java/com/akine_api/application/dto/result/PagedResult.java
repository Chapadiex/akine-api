package com.akine_api.application.dto.result;

import java.util.List;

public record PagedResult<T>(
        List<T> content,
        int page,
        int size,
        long total
) {}

