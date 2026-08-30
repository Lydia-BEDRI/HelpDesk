package com.helpdesk.ticket.mapper;

import org.springframework.stereotype.Component;
import com.helpdesk.ticket.dto.TicketResponse;
import com.helpdesk.ticket.entity.Ticket;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TicketMapper {

    private final CategoryMapper categoryMapper;

    public TicketResponse toResponse(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .category(categoryMapper.toResponse(ticket.getCategory()))
                .creator(toUserInfo(ticket.getCreator()))
                .assignee(toUserInfo(ticket.getAssignee()))
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }

    private TicketResponse.UserInfo toUserInfo(com.helpdesk.auth.entity.User user) {
        if (user == null) {
            return null;
        }
        return TicketResponse.UserInfo.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
