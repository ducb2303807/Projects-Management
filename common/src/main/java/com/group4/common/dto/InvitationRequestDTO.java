package com.group4.common.dto;

import com.group4.common.enums.InvitationAction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvitationRequestDTO {

    @Schema(
            description = "Hành động phản hồi lời mời",
            example = "ACCEPT",
            allowableValues = {"ACCEPT", "DECLINE"}
    )
    private InvitationAction action;
}
