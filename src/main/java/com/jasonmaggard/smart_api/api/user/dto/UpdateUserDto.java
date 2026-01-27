package com.jasonmaggard.smart_api.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for updating a user")
public class UpdateUserDto {
    
    @Schema(description = "Full name of the user", example = "John Doe")
    private String name;
    
    @Schema(description = "Email address", example = "john@example.com")
    @Email(message = "Email must be valid")
    private String email;
}
