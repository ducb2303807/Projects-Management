package com.group4.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserExistsRequestDTO {
    @NotBlank( message = "Username cannot be blank")
    @NotEmpty( message = "Username cannot be empty")
    @Size(min = 5, message = "Username must be at least 5 characters long")
    private String username;
    @Email
    private String email;
}
