package com.group4.projects_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USER_WIDGET_CONFIG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWidgetConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_WIDGET_CONFIG_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "PLUGIN_ID", nullable = false)
    private Plugin plugin;

    @Column(name = "POS_X")
    private int posX;

    @Column(name = "POS_Y")
    private int posY;

    @Column(name = "IS_VISIBLE")
    private Boolean isVisible;
}