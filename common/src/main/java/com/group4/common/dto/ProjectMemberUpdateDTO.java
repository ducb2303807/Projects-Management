package com.group4.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMemberUpdateDTO {
    public enum MemberStatus {
        ACCEPTED,
        DECLINED,
        PENDING,
        LEFT,
        REMOVED
    }

    @NotNull(message = "Trạng thái không được để trống")
    private MemberStatus status;
}
