package com.akine_api.interfaces.api.v1.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendActivationRequest(@NotBlank @Email String email) {}
