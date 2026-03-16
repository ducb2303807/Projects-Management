package com.group4.common.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectInvitationRequestDTO {
    @NotNull
    private Long projectId;
    @NotNull
    private Long inviteeId;
    @NotNull
    private Long roleId;
}
