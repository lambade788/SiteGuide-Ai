package com.siteguard.service;

import com.siteguard.entity.Camera;
import com.siteguard.entity.Site;
import com.siteguard.exception.EntityNotFoundException;
import com.siteguard.repository.CameraRepository;

import java.util.List;

public class CameraService {

    private final CameraRepository cameraRepository;
    private final SiteService siteService;

    public CameraService() {
        this.cameraRepository = new CameraRepository();
        this.siteService = new SiteService();
    }

    public CameraService(CameraRepository cameraRepository, SiteService siteService) {
        this.cameraRepository = cameraRepository;
        this.siteService = siteService;
    }

    public Camera createCamera(Long siteId, String cameraName, String cameraUrl, String location, Camera.Status status) {
        Site site = siteService.getSiteById(siteId);
        if (cameraName == null || cameraName.trim().isEmpty()) {
            throw new IllegalArgumentException("Camera name cannot be empty.");
        }
        Camera camera = new Camera(site, cameraName.trim(), cameraUrl, location, status);
        return cameraRepository.save(camera);
    }

    public Camera getCameraById(Long id) {
        return cameraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Camera", id));
    }

    public List<Camera> getAllCameras() {
        return cameraRepository.findAll();
    }

    public List<Camera> getCamerasBySite(Long siteId) {
        return cameraRepository.findBySiteId(siteId);
    }
}
