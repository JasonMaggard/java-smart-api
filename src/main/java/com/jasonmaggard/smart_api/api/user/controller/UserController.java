package com.jasonmaggard.smart_api.api.user.controller;

import com.jasonmaggard.smart_api.api.user.dto.CreateUserDto;
import com.jasonmaggard.smart_api.api.user.dto.UpdateUserDto;
import com.jasonmaggard.smart_api.api.user.entity.User;
import com.jasonmaggard.smart_api.api.user.service.UserService;
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
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "users", description = "User management endpoints")
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    public ResponseEntity<User> create(@Valid @RequestBody CreateUserDto createUserDto) {
        User user = userService.create(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    @GetMapping
    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully")
    public ResponseEntity<List<User>> findAll() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get a user by ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<User> findOne(@PathVariable @NonNull UUID id) {
        Objects.requireNonNull(id, "User ID cannot be null");
        User user = userService.findOne(id);
        return ResponseEntity.ok(user);
    }
    
    @PatchMapping("/{id}")
    @Operation(summary = "Update a user")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<User> update(
            @PathVariable @NonNull UUID id,
            @Valid @RequestBody @NonNull UpdateUserDto updateUserDto) {
        Objects.requireNonNull(id, "User ID cannot be null");
        Objects.requireNonNull(updateUserDto, "UpdateUserDto cannot be null");
        User user = userService.update(id, updateUserDto);
        return ResponseEntity.ok(user);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Void> remove(@PathVariable @NonNull UUID id) {
        Objects.requireNonNull(id, "User ID cannot be null");
        userService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
