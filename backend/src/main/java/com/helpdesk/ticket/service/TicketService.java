package com.helpdesk.ticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.helpdesk.auth.entity.User;
import com.helpdesk.auth.repository.UserRepository;
import com.helpdesk.ticket.dto.CreateTicketRequest;
import com.helpdesk.ticket.dto.TicketResponse;
import com.helpdesk.ticket.dto.UpdateTicketRequest;
import com.helpdesk.ticket.entity.Category;
import com.helpdesk.ticket.entity.Ticket;
import com.helpdesk.ticket.entity.TicketStatus;
import com.helpdesk.ticket.mapper.TicketMapper;
import com.helpdesk.ticket.repository.CategoryRepository;
import com.helpdesk.ticket.repository.TicketRepository;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;

    public Page<TicketResponse> getAll(Pageable pageable) {
        return ticketRepository.findAll(pageable)
                .map(ticketMapper::toResponse);
    }

    public Page<TicketResponse> search(String search, Pageable pageable) {
        return ticketRepository.searchByTitleOrDescription(search, pageable)
                .map(ticketMapper::toResponse);
    }

    public TicketResponse getById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé"));
        return ticketMapper.toResponse(ticket);
    }

    public TicketResponse create(CreateTicketRequest request) {
        User currentUser = getCurrentUser();
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));

        Ticket ticket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .category(category)
                .creator(currentUser)
                .status(TicketStatus.OPEN)
                .build();

        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    public TicketResponse update(Long id, UpdateTicketRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé"));

        if (request.getTitle() != null) {
            ticket.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            ticket.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            ticket.setPriority(request.getPriority());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
            ticket.setCategory(category);
        }

        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    public void delete(Long id) {
        ticketRepository.deleteById(id);
    }

    public TicketResponse updateStatus(Long id, TicketStatus newStatus) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé"));

        validateStatusTransition(ticket.getStatus(), newStatus);
        ticket.setStatus(newStatus);
        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    public TicketResponse assignTicket(Long id, Long assigneeId) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé"));
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        ticket.setAssignee(assignee);
        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    private void validateStatusTransition(TicketStatus currentStatus, TicketStatus newStatus) {
        boolean isValid = switch (currentStatus) {
            case OPEN -> newStatus == TicketStatus.IN_PROGRESS;
            case IN_PROGRESS -> newStatus == TicketStatus.WAITING_FOR_USER || newStatus == TicketStatus.RESOLVED;
            case WAITING_FOR_USER -> newStatus == TicketStatus.IN_PROGRESS;
            case RESOLVED -> newStatus == TicketStatus.CLOSED;
            case CLOSED -> false;
        };

        if (!isValid) {
            throw new RuntimeException("Transition de statut invalide : " + currentStatus + " -> " + newStatus);
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}
