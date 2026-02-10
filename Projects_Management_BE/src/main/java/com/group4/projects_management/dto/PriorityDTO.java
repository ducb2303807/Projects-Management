package com.group4.projects_management.dto;

public class PriorityDTO {
    private Long priorityId;
    private String priorityName;
    private String priorityDescription;

    public PriorityDTO() {}

    public PriorityDTO(Long priorityId, String priorityName, String priorityDescription) {
        this.priorityId = priorityId;
        this.priorityName = priorityName;
        this.priorityDescription = priorityDescription;
    }

    public Long getPriorityId() { return priorityId; }
    public void setPriorityId(Long priorityId) { this.priorityId = priorityId; }

    public String getPriorityName() { return priorityName; }
    public void setPriorityName(String priorityName) { this.priorityName = priorityName; }

    public String getPriorityDescription() { return priorityDescription; }
    public void setPriorityDescription(String priorityDescription) { this.priorityDescription = priorityDescription; }
}
