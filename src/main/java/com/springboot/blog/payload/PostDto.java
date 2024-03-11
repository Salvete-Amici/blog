package com.springboot.blog.payload;

import lombok.Data;

import java.util.Set;

@Data // internally generates getters, setters, etc. for all fields
public class PostDto {
    private long id;
    private String title;
    private String description;
    private String content;
    private Set<CommentDto> comments;
}
