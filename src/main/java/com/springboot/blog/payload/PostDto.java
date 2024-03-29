package com.springboot.blog.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data // internally generates getters, setters, etc. for all fields
public class PostDto {
    private long id;
    @NotEmpty
    @Size(min = 2, message = "Post title should have at least 2 chars")
    private String title;
    @NotEmpty
    @Size(min = 10, message = "Post description should have at least 10 chars")
    private String description;
    @NotEmpty
    private String content;
    private Set<CommentDto> comments;
}
