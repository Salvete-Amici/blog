package com.springboot.blog.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// adding annotation so no need to add getters, setters, etc.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    // would like to provide these fields in response of the Pagination API
    private List<PostDto> content;
    private int pageNum;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
