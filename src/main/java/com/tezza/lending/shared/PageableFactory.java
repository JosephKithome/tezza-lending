package com.tezza.lending.shared;

import com.tezza.lending.exception.BusinessRuleException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageableFactory {
    private static final int MAX_SIZE = 100;
    private static final String DEFAULT_SORT_FIELD = "id";

    private PageableFactory() {
    }

    public static Pageable of(int page, int size, String sortBy, String sortDirection) {
        if (page < 0) {
            throw new BusinessRuleException("Page must be zero or greater");
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new BusinessRuleException("Size must be between 1 and " + MAX_SIZE);
        }
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection)
                .orElse(Sort.Direction.DESC);
        String sortField = isBlank(sortBy) ? DEFAULT_SORT_FIELD : sortBy;

        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }

    public static String requestPayload(int page, int size, String sortBy, String sortDirection) {
        return "page=" + page
                + ",size=" + size
                + ",sortBy=" + sortBy
                + ",sortDirection=" + sortDirection;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
