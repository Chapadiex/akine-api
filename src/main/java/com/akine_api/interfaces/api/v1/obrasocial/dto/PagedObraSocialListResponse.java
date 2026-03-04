package com.akine_api.interfaces.api.v1.obrasocial.dto;

import java.util.List;

public record PagedObraSocialListResponse(
        List<ObraSocialListItemResponse> content,
        int page,
        int size,
        long total
) {}

