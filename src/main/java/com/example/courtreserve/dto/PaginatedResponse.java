package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private List<T> content;
}