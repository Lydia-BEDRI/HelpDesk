package com.helpdesk.ticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.helpdesk.ticket.dto.CategoryResponse;
import com.helpdesk.ticket.entity.Category;
import com.helpdesk.ticket.mapper.CategoryMapper;
import com.helpdesk.ticket.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
        return categoryMapper.toResponse(category);
    }

    public CategoryResponse create(String name, String description) {
        Category category = Category.builder()
                .name(name)
                .description(description)
                .active(true)
                .build();
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public CategoryResponse update(Long id, String name, String description) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
        if (name != null) {
            category.setName(name);
        }
        if (description != null) {
            category.setDescription(description);
        }
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
