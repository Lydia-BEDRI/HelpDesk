package com.helpdesk.ticket.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.helpdesk.ticket.dto.CreateTicketRequest;
import com.helpdesk.ticket.dto.TicketResponse;
import com.helpdesk.ticket.dto.UpdateTicketRequest;
import com.helpdesk.ticket.entity.TicketStatus;
import com.helpdesk.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Gestion des tickets IT")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    @Operation(summary = "Lister tous les tickets avec pagination")
    public ResponseEntity<Page<TicketResponse>> getAll(
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ticketService.getAll(pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des tickets par titre ou description")
    public ResponseEntity<Page<TicketResponse>> search(
            @RequestParam String q,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ticketService.search(q, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un ticket par ID")
    public ResponseEntity<TicketResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'AGENT', 'ADMIN')")
    @Operation(summary = "Créer un nouveau ticket")
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.create(request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'AGENT', 'ADMIN')")
    @Operation(summary = "Modifier un ticket")
    public ResponseEntity<TicketResponse> update(@PathVariable Long id, @RequestBody UpdateTicketRequest request) {
        return ResponseEntity.ok(ticketService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Supprimer un ticket")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Changer le statut d'un ticket")
    public ResponseEntity<TicketResponse> updateStatus(@PathVariable Long id, @RequestBody StatusUpdate statusUpdate) {
        return ResponseEntity.ok(ticketService.updateStatus(id, statusUpdate.getStatus()));
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Assigner un ticket à un agent")
    public ResponseEntity<TicketResponse> assignTicket(@PathVariable Long id, @RequestBody AssignRequest assignRequest) {
        return ResponseEntity.ok(ticketService.assignTicket(id, assignRequest.getAssigneeId()));
    }

    public static class StatusUpdate {
        private TicketStatus status;

        public TicketStatus getStatus() {
            return status;
        }

        public void setStatus(TicketStatus status) {
            this.status = status;
        }
    }

    public static class AssignRequest {
        private Long assigneeId;

        public Long getAssigneeId() {
            return assigneeId;
        }

        public void setAssigneeId(Long assigneeId) {
            this.assigneeId = assigneeId;
        }
    }
}
