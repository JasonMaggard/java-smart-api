package com.jasonmaggard.smart_api.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for creating a new user")
public class CreateUserDto {
    
    @Schema(description = "Full name of the user", example = "John Doe")
    @NotBlank(message = "Name is required")
    private String name;
    
    @Schema(description = "Email address", example = "john@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
}
