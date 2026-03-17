package com.group4.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvitationRequestDTO {
    public enum InvitationType {
        ACCEPT,
        DECLINE
    };
    public InvitationType type;
}
