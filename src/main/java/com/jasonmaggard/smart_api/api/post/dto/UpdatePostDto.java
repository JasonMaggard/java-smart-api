package com.jasonmaggard.smart_api.api.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for updating a post")
public class UpdatePostDto {
    
    @Schema(description = "Post title", example = "My Updated Blog Post")
    private String title;
    
    @Schema(description = "Post content", example = "This is the updated content...")
    private String content;
    
    @Schema(description = "ID of the user who created the post")
    private UUID userId;
}
