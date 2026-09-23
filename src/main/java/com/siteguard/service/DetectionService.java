package com.siteguard.service;

import com.siteguard.entity.Camera;
import com.siteguard.entity.Detection;
import com.siteguard.entity.Worker;
import com.siteguard.exception.EntityNotFoundException;
import com.siteguard.repository.DetectionRepository;

import java.math.BigDecimal;
import java.util.List;

public class DetectionService {

    private final DetectionRepository detectionRepository;

    public DetectionService() {
        this.detectionRepository = new DetectionRepository();
    }

    public DetectionService(DetectionRepository detectionRepository) {
        this.detectionRepository = detectionRepository;
    }

    public Detection recordDetection(Camera camera, Worker worker, String objectType, BigDecimal confidence, String imageUrl, String metadata) {
        if (objectType == null || objectType.trim().isEmpty()) {
            throw new IllegalArgumentException("Object type cannot be empty.");
        }
        Detection detection = new Detection(camera, worker, objectType.trim(), confidence, imageUrl, metadata);
        return detectionRepository.save(detection);
    }

    public Detection getDetectionById(Long id) {
        return detectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Detection", id));
    }

    public List<Detection> getAllDetections() {
        return detectionRepository.findAll();
    }

    public List<Detection> getDetectionsByCamera(Long cameraId) {
        return detectionRepository.findByCameraId(cameraId);
    }

    public List<Detection> getDetectionsByWorker(Long workerId) {
        return detectionRepository.findByWorkerId(workerId);
    }
}
