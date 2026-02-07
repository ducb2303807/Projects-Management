package com.group4.projects_management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.group4.common.dto.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    @GetMapping("/")
    public List<ProjectDTO> GetAll() {
        return null;
    }
}
