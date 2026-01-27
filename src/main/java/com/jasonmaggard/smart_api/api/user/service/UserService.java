package com.jasonmaggard.smart_api.api.user.service;

import com.jasonmaggard.smart_api.api.user.dto.CreateUserDto;
import com.jasonmaggard.smart_api.api.user.dto.UpdateUserDto;
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
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional
    public User create(CreateUserDto createUserDto) {
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + createUserDto.getEmail());
        }
        
        User user = new User();
        user.setName(createUserDto.getName());
        user.setEmail(createUserDto.getEmail());
        
        return userRepository.save(user);
    }
    
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public User findOne(@NonNull UUID id) {
        Objects.requireNonNull(id, "User ID cannot be null");
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }
    
    @Transactional
    public User update(@NonNull UUID id, @NonNull UpdateUserDto updateUserDto) {
        User user = findOne(id);
        
        if (updateUserDto.getName() != null) {
            user.setName(updateUserDto.getName());
        }
        
        if (updateUserDto.getEmail() != null) {
            if (!user.getEmail().equals(updateUserDto.getEmail()) && 
                userRepository.existsByEmail(updateUserDto.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + updateUserDto.getEmail());
            }
            user.setEmail(updateUserDto.getEmail());
        }
        
        @SuppressWarnings("null") // JPA save is guaranteed to return non-null for managed entities
        User savedUser = userRepository.save(user);
        return Objects.requireNonNull(savedUser, "Failed to save user");
    }
    
    @Transactional
    public void remove(@NonNull UUID id) {
        User user = findOne(id);
        Objects.requireNonNull(user, "User not found for deletion");
        userRepository.delete(user);
    }
}
