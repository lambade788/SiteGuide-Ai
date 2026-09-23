package com.siteguard.service;

import com.siteguard.entity.Project;
import com.siteguard.exception.EntityNotFoundException;
import com.siteguard.repository.ProjectRepository;

import java.time.LocalDate;
import java.util.List;

public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService() {
        this.projectRepository = new ProjectRepository();
    }

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(String name, String description, String clientName, LocalDate startDate, LocalDate expectedEnd, Project.Status status) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        Project project = new Project(name, description, clientName, startDate, expectedEnd, status);
        return projectRepository.save(project);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project", id));
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getActiveProjects() {
        return projectRepository.findActiveProjects();
    }
}
