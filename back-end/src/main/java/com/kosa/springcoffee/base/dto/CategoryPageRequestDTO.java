package com.kosa.springcoffee.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Builder
@AllArgsConstructor
@Data
public class CategoryPageRequestDTO {

    private int page;
    private int size;
    private String category;

    public CategoryPageRequestDTO() {
        this.page = 1;
        this.size = 10;
    }

    public Pageable getPageable(Sort sort) {
        return PageRequest.of(page-1, size, sort);
    }
}
