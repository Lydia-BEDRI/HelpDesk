package com.helpdesk.auth.dto;

import com.helpdesk.auth.UserRole;

public record AuthResponse(String token, UserSummary user) {
    public record UserSummary(Long id, String firstName, String lastName, String email, UserRole role) {
    }
}
