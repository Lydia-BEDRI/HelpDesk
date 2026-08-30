package com.helpdesk.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.helpdesk.ticket.entity.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketRequest {

    @NotBlank(message = "Le titre est requis")
    @Size(min = 5, max = 150, message = "Le titre doit faire entre 5 et 150 caractères")
    private String title;

    @NotBlank(message = "La description est requise")
    @Size(min = 10, message = "La description doit faire au minimum 10 caractères")
    private String description;

    @NotNull(message = "La priorité est requise")
    private Priority priority;

    @NotNull(message = "La catégorie est requise")
    private Long categoryId;
}
