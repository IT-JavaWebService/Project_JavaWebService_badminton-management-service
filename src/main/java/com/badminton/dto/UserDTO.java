package com.badminton.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    private String role;
    private boolean active;
    private LocalDateTime createdAt;
}
