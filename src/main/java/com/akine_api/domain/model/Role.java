package com.akine_api.domain.model;

import java.util.UUID;

public class Role {

    private final UUID id;
    private final RoleName name;
    private final String description;

    public Role(UUID id, RoleName name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public UUID getId() { return id; }
    public RoleName getName() { return name; }
    public String getDescription() { return description; }
}
