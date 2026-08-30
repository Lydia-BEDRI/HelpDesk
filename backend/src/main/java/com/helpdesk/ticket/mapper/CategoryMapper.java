package com.helpdesk.ticket.mapper;

import org.springframework.stereotype.Component;
import com.helpdesk.ticket.dto.CategoryResponse;
import com.helpdesk.ticket.entity.Category;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .build();
    }
}
