package com.akine_api.domain.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {

    private final UUID id;
    private final String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String phone;
    private UserStatus status;
    private final Set<Role> roles;
    private final Instant createdAt;
    private Instant updatedAt;

    public User(UUID id, String email, String passwordHash,
                String firstName, String lastName, String phone,
                UserStatus status, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.status = status;
        this.roles = new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void markPending() {
        this.status = UserStatus.PENDING;
        this.updatedAt = Instant.now();
    }

    public void rejectActivation() {
        this.status = UserStatus.REJECTED;
        this.updatedAt = Instant.now();
    }

    public void updateProfile(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.updatedAt = Instant.now();
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return UserStatus.ACTIVE.equals(this.status);
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    // Getters
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public UserStatus getStatus() { return status; }
    public Set<Role> getRoles() { return Set.copyOf(roles); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
