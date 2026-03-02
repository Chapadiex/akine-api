package com.akine_api.domain.exception;

public class RoleNotFoundException extends DomainException {
    public RoleNotFoundException(String roleName) {
        super("Rol no encontrado: " + roleName);
    }
}
