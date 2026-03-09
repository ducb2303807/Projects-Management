package com.group4.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserExistsResponseDTO {
    private boolean usernameExists;
    private boolean emailExists;
}
