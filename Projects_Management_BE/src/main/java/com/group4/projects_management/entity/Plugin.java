package com.group4.projects_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PLUGIN")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plugin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PLUGIN_ID")
    private Long id;

    @Column(name = "PLUGIN_NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "PLUGIN_VERSION", nullable = false, columnDefinition = "TEXT")
    private String version;

    @Column(name = "PLUGIN_DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Column(name = "PLUGIN_IS_ACTIVE", nullable = false)
    private boolean isActive;

    @Column(name = "PLUGIN_PATH", nullable = false, columnDefinition = "TEXT")
    private String path;

    @Column(name = "PLUGIN_MAIN_CLASS", nullable = false, columnDefinition = "TEXT")
    private String mainClass;
}
