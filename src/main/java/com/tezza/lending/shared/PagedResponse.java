package com.tezza.lending.shared;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponse(
        List<?> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean empty
) {
    public static PagedResponse from(Page<?> page) {
        return new PagedResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }
}
