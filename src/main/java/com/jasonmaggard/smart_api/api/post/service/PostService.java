package com.jasonmaggard.smart_api.api.post.service;

import com.jasonmaggard.smart_api.api.post.dto.CreatePostDto;
import com.jasonmaggard.smart_api.api.post.dto.UpdatePostDto;
import com.jasonmaggard.smart_api.api.post.entity.Post;
import com.jasonmaggard.smart_api.api.post.repository.PostRepository;
import com.jasonmaggard.smart_api.api.user.entity.User;
import com.jasonmaggard.smart_api.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public Post create(@NonNull CreatePostDto createPostDto) {
        UUID userId = Objects.requireNonNull(createPostDto.getUserId(), "User ID cannot be null");
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        Post post = new Post();
        post.setTitle(createPostDto.getTitle());
        post.setContent(createPostDto.getContent());
        post.setUser(user);
        
        return postRepository.save(post);
    }
    
    @Transactional(readOnly = true)
    public List<Post> findAll() {
        return postRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Post findOne(@NonNull UUID id) {
        Objects.requireNonNull(id, "Post ID cannot be null");
        return postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
    }
    
    @Transactional
    public Post update(@NonNull UUID id, @NonNull UpdatePostDto updatePostDto) {
        Post post = findOne(id);
        
        if (updatePostDto.getTitle() != null) {
            post.setTitle(updatePostDto.getTitle());
        }
        
        if (updatePostDto.getContent() != null) {
            post.setContent(updatePostDto.getContent());
        }
        
        if (updatePostDto.getUserId() != null) {
            UUID userId = Objects.requireNonNull(updatePostDto.getUserId(), "User ID cannot be null");
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
            post.setUser(user);
        }
        
        @SuppressWarnings("null") // JPA save is guaranteed to return non-null for managed entities
        Post savedPost = postRepository.save(post);
        return Objects.requireNonNull(savedPost, "Failed to save post");
    }
    
    @Transactional
    public void remove(@NonNull UUID id) {
        Post post = findOne(id);
        Objects.requireNonNull(post, "Post not found for deletion");
        postRepository.delete(post);
    }
    
    @Transactional(readOnly = true)
    public List<Post> findByUserId(@NonNull UUID userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        return postRepository.findByUserId(userId);
    }
}
