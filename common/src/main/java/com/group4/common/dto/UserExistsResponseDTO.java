package com.group4.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserExistsResponseDTO {
    private boolean usernameExists;
    private boolean emailExists;
}
