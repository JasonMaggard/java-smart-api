package com.jasonmaggard.smart_api.api.post.controller;

import com.jasonmaggard.smart_api.api.post.dto.CreatePostDto;
import com.jasonmaggard.smart_api.api.post.dto.UpdatePostDto;
import com.jasonmaggard.smart_api.api.post.entity.Post;
import com.jasonmaggard.smart_api.api.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "posts", description = "Post management endpoints")
public class PostController {
    
    private final PostService postService;
    
    @PostMapping
    @Operation(summary = "Create a new post")
    @ApiResponse(responseCode = "201", description = "Post created successfully")
    public ResponseEntity<Post> create(@Valid @RequestBody @NonNull CreatePostDto createPostDto) {
        Objects.requireNonNull(createPostDto, "CreatePostDto cannot be null");
        Post post = postService.create(createPostDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }
    
    @GetMapping
    @Operation(summary = "Get all posts")
    @ApiResponse(responseCode = "200", description = "List of posts retrieved successfully")
    public ResponseEntity<List<Post>> findAll() {
        List<Post> posts = postService.findAll();
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get a post by ID")
    @ApiResponse(responseCode = "200", description = "Post found")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public ResponseEntity<Post> findOne(@PathVariable @NonNull UUID id) {
        Objects.requireNonNull(id, "Post ID cannot be null");
        Post post = postService.findOne(id);
        return ResponseEntity.ok(post);
    }
    
    @PatchMapping("/{id}")
    @Operation(summary = "Update a post")
    @ApiResponse(responseCode = "200", description = "Post updated successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public ResponseEntity<Post> update(
            @PathVariable @NonNull UUID id,
            @Valid @RequestBody @NonNull UpdatePostDto updatePostDto) {
        Objects.requireNonNull(id, "Post ID cannot be null");
        Objects.requireNonNull(updatePostDto, "UpdatePostDto cannot be null");
        Post post = postService.update(id, updatePostDto);
        return ResponseEntity.ok(post);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a post")
    @ApiResponse(responseCode = "204", description = "Post deleted successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public ResponseEntity<Void> remove(@PathVariable @NonNull UUID id) {
        Objects.requireNonNull(id, "Post ID cannot be null");
        postService.remove(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all posts by a user")
    @ApiResponse(responseCode = "200", description = "List of user posts retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<List<Post>> findByUserId(@PathVariable @NonNull UUID userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        List<Post> posts = postService.findByUserId(userId);
        return ResponseEntity.ok(posts);
    }
}
