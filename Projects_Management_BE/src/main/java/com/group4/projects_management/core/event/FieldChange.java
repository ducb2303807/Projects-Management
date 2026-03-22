package com.group4.projects_management.core.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldChange {
    private String columnName;
    private String oldValue;
    private String newValue;
}
