package com.siteguard.service;

import com.siteguard.entity.Project;
import com.siteguard.entity.Site;
import com.siteguard.exception.EntityNotFoundException;
import com.siteguard.repository.SiteRepository;

import java.util.List;

public class SiteService {

    private final SiteRepository siteRepository;
    private final ProjectService projectService;

    public SiteService() {
        this.siteRepository = new SiteRepository();
        this.projectService = new ProjectService();
    }

    public SiteService(SiteRepository siteRepository, ProjectService projectService) {
        this.siteRepository = siteRepository;
        this.projectService = projectService;
    }

    public Site createSite(Long projectId, String name, String location, Site.Status status) {
        Project project = projectService.getProjectById(projectId);
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Site name cannot be empty.");
        }
        Site site = new Site(project, name.trim(), location, status);
        return siteRepository.save(site);
    }

    public Site getSiteById(Long id) {
        return siteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Site", id));
    }

    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }

    public List<Site> getSitesByProject(Long projectId) {
        return siteRepository.findByProjectId(projectId);
    }
}
