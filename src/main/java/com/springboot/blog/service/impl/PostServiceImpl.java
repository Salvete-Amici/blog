package com.springboot.blog.service.impl;

import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.model.Category;
import com.springboot.blog.model.Post;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service // indicates class is service class -> available for component detection during component scanning
public class PostServiceImpl implements PostService {

    // constructor-based dependency injection
    // whenever we configure our class as spring bean and the class has only one constructor, then can omit the @Autowired annotation
    private PostRepository postRepository;

    private ModelMapper mapper;

    private CategoryRepository categoryRepository;

    public PostServiceImpl(PostRepository postRepository, ModelMapper mapper, CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public PostDto createPost(PostDto postDto) {

        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        //convert DTO to entity
//        Post post = new Post(); // create post object
//        post.setTitle(postDto.getTitle());
//        post.setDescription(postDto.getDescription());
//        post.setContent(postDto.getContent());
        Post post = mapToEntity(postDto);
        post.setCategory(category);
        Post newPost = postRepository.save(post); // save post entity into database (save method returns a new post)

        // convert entity to DTO (send the saved post details to API using PostDto)
//        PostDto postResponse = new PostDto();
//        postResponse.setId(newPost.getId());
//        postResponse.setTitle(newPost.getTitle());
//        postResponse.setDescription(newPost.getDescription());
//        postResponse.setContent(newPost.getContent());

        return mapToDTO(newPost);
    }

    @Override
    public PostResponse getAllPosts(int pageNum, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase((Sort.Direction.ASC.name())) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<Post> posts = postRepository.findAll(pageable);

        // get content for page object
        List<Post> listOfPosts = posts.getContent();

        //WT convert list of posts into list of postDtos
        List<PostDto> content = listOfPosts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
        PostResponse postResponse = new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNum(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());

        return postResponse;
    }

    @Override
    public PostDto getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return mapToDTO(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {
        // get post by id from the database
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        Category category = categoryRepository.findById(postDto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());
        post.setCategory(category);

        Post updatedPost = postRepository.save(post);
        return mapToDTO(updatedPost);
    }

    @Override
    public void deletePostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        postRepository.delete(post);
    }

    @Override
    public List<PostDto> getPostsByCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        List<Post> posts = postRepository.findByCategoryId(categoryId);

        return posts.stream().map((post) -> mapToDTO(post))
                .collect(Collectors.toList());
    }

    // convert entity into DTO -> refactor our code
    private PostDto mapToDTO(Post post) {
        PostDto postDto = mapper.map(post, PostDto.class);
//
//        PostDto postDto = new PostDto();
//        postDto.setId(post.getId());
//        postDto.setTitle(post.getTitle());
//        postDto.setDescription(post.getDescription());
//        postDto.setContent(post.getContent());
        return postDto;
    }

    // convert DTO to Entity
    private Post mapToEntity(PostDto postDto) {
        Post post = mapper.map(postDto, Post.class); // format: mapper.map(source, destination)

//        Post post = new Post(); // create post object
//        post.setTitle(postDto.getTitle());
//        post.setDescription(postDto.getDescription());
//        post.setContent(postDto.getContent());
        return post;

    }
}
