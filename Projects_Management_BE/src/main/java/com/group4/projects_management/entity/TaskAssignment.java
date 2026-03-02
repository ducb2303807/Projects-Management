package com.group4.projects_management.entity; /***********************************************************************
 * Module:  TaskAssignment.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskAssignment
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @pdOid 907e8057-a1d1-40ac-b813-54369d217054
 */
@Entity
@Table(
        name = "TASK_ASSIGNMENT",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"TASK_ID", "TASK_ASSIGNEE"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TASK_ASSIGNMENT_ID")
    private Long id;

    @Column(name = "TASK_ASSIGNED_AT", nullable = false)
    private LocalDateTime assignAt;

    @ManyToOne
    @JoinColumn(name = "TASK_ASSIGNEE", nullable = false) // FK đến ProjectMember
    @ToString.Exclude
    private ProjectMember assignee;

    @ManyToOne
    @JoinColumn(name = "TASK_ASSIGNER", nullable = false) // FK đến ProjectMember
    @ToString.Exclude
    private ProjectMember assigner;

    @ManyToOne
    @JoinColumn(name = "TASK_ID", nullable = false)
    @ToString.Exclude
    private Task task;

    @PrePersist
    protected void onAssign() {
        this.assignAt = LocalDateTime.now();
    }

    public long getAssignmentDuration() {
        if (this.assignAt == null) {
            return 0;
        }
        return java.time.Duration
                .between(this.assignAt, LocalDateTime.now())
                .toDays();
    }

    /**
     * @param projectMemberId
     * @pdOid 04228640-0f06-4a07-9454-2fc3f6077444
     */
    public boolean isAssignee(Long projectMemberId) {
        return this.assignee != null
                && this.assignee.getId().equals(projectMemberId);
    }

    /**
     * @param newAssignee
     * @param assigner
     * @pdOid 30b58a2f-27e5-4cad-acf2-60e4d5d3c4e9
     */
    public void reassign(ProjectMember newAssignee, ProjectMember assigner) {
        if (newAssignee == null) {
            throw new IllegalArgumentException("New assignee cannot be null");
        }
        if (assigner == null) {
            throw new IllegalArgumentException("Assigner cannot be null");
        }
        if (this.assignee != null && this.assignee.equals(newAssignee)) {
            return;
        }

        this.assignee = newAssignee;
        this.assigner = assigner;
        this.assignAt = LocalDateTime.now();
    }
}