package com.sharemate.util;

import com.sharemate.exception.ValidationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class Pagination {
    public static Pageable of(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Параметр from не может быть отрицательным, а size должен быть больше нуля");
        }
        return PageRequest.of(from / size, size);
    }
    private Pagination() {
    }
}
