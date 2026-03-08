package com.akine_api.application.dto.result;

import com.akine_api.domain.model.HistoriaClinicaTimelineEventType;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistoriaClinicaTimelineEventResult(
        String eventId,
        HistoriaClinicaTimelineEventType type,
        LocalDateTime occurredAt,
        UUID profesionalId,
        String profesionalNombre,
        String title,
        String summary,
        String statusLabel,
        UUID relatedEntityId
) {}
