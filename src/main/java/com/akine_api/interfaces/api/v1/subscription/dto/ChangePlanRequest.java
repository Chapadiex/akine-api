package com.akine_api.interfaces.api.v1.subscription.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePlanRequest(@NotBlank String planCode) {}
