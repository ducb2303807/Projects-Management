package com.group4.projects_management.mapper;

import com.group4.common.dto.TaskCeateRequestDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.projects_management.entity.Priority;
import com.group4.projects_management.entity.Project;
import com.group4.projects_management.entity.Task;
import com.group4.projects_management.entity.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Mapper(componentModel = "spring", uses = {TaskAssignmentMapper.class})
public abstract class TaskMapper {

    // --- 1. Map từ DTO và các Entity phụ sang Task ---
    @Mapping(target = "id", ignore = true) // ID tự tăng, không map
    @Mapping(target = "createdAt", ignore = true) // Thời gian tạo tự sinh
    @Mapping(target = "assignments", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "historys", ignore = true)
    @Mapping(target = "project", source = "project")
    @Mapping(target = "priority", source = "priority")
    @Mapping(target = "taskStatus", source = "status")
    @Mapping(target = "deadline", expression = "java(fixMidnightDeadline(dto.getDeadline()))")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "description", source = "dto.description")
    public abstract Task toEntity(TaskCeateRequestDTO dto, Project project, Priority priority, TaskStatus status);


    // --- 2. Map từ Task sang Response DTO ---
    @Mapping(target = "taskId", source = "id")
    @Mapping(target = "priorityName", source = "priority.name")
    @Mapping(target = "statusName", source = "taskStatus.name")
    @Mapping(target = "assignees", source = "assignments")
    public abstract TaskResponseDTO toDto(Task task);


    // --- 3. Hàm tiện ích xử lý "Bẫy 0 giờ sáng" giấu ngay trong Mapper ---
    public LocalDateTime fixMidnightDeadline(LocalDateTime deadline) {
        if (deadline == null) return null;
        if (deadline.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return deadline.withHour(23).withMinute(59).withSecond(59);
        }
        return deadline;
    }
}
