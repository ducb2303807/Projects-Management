package com.group4.projects_management.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDTO {
    private Long projectId;
    private Long projectCreateById;
    private Long projectStatusId;
    private String projectName;
    private String projectDescription;
    private LocalDateTime projectStartAt;
    private LocalDateTime projectEndAt;
    private LocalDateTime projectCreatedAt;
    private LocalDateTime projectUpdateAt;
}
