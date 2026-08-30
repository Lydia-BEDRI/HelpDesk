package com.helpdesk.ticket.dto;

import com.helpdesk.ticket.entity.Priority;
import com.helpdesk.ticket.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {

    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private Priority priority;
    private CategoryResponse category;
    private UserInfo creator;
    private UserInfo assignee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
    }
}
