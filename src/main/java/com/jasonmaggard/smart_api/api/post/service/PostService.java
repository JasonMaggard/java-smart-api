package com.jasonmaggard.smart_api.api.post.service;

import com.jasonmaggard.smart_api.api.post.dto.CreatePostDto;
import com.jasonmaggard.smart_api.api.post.dto.UpdatePostDto;
import com.jasonmaggard.smart_api.api.post.entity.Post;
import com.jasonmaggard.smart_api.api.post.repository.PostRepository;
import com.jasonmaggard.smart_api.api.user.entity.User;
import com.jasonmaggard.smart_api.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public Post create(CreatePostDto createPostDto) {
        User user = userRepository.findById(createPostDto.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + createPostDto.getUserId()));
        
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
    public Post findOne(UUID id) {
        return postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
    }
    
    @Transactional
    public Post update(UUID id, UpdatePostDto updatePostDto) {
        Post post = findOne(id);
        
        if (updatePostDto.getTitle() != null) {
            post.setTitle(updatePostDto.getTitle());
        }
        
        if (updatePostDto.getContent() != null) {
            post.setContent(updatePostDto.getContent());
        }
        
        if (updatePostDto.getUserId() != null) {
            User user = userRepository.findById(updatePostDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + updatePostDto.getUserId()));
            post.setUser(user);
        }
        
        return postRepository.save(post);
    }
    
    @Transactional
    public void remove(UUID id) {
        Post post = findOne(id);
        postRepository.delete(post);
    }
    
    @Transactional(readOnly = true)
    public List<Post> findByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        return postRepository.findByUserId(userId);
    }
}
