package com.group4.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserExistsRequestDTO {
    @NotBlank( message = "Username is required")
    private String username;
    @Email
    @NotBlank( message = "Email is required")
    private String email;
}
