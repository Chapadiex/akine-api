package com.akine_api.application.port.output;

import com.akine_api.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface TokenGeneratorPort {
    String generateAccessToken(User user, List<UUID> consultorioIds);
    String generateRefreshToken();
    String extractEmailFromAccessToken(String token);
    boolean validateAccessToken(String token);
}
