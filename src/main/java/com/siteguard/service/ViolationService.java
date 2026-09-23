package com.siteguard.service;

import com.siteguard.entity.Detection;
import com.siteguard.entity.SafetyRule;
import com.siteguard.entity.Violation;
import com.siteguard.entity.Worker;
import com.siteguard.exception.EntityNotFoundException;
import com.siteguard.repository.ViolationRepository;

import java.math.BigDecimal;
import java.util.List;

public class ViolationService {

    private final ViolationRepository violationRepository;

    public ViolationService() {
        this.violationRepository = new ViolationRepository();
    }

    public ViolationService(ViolationRepository violationRepository) {
        this.violationRepository = violationRepository;
    }

    public Violation createViolation(Detection detection, Worker worker, SafetyRule safetyRule,
                                    String violationType, Violation.Severity severity,
                                    String description, BigDecimal confidence, String imageUrl) {
        if (violationType == null || violationType.trim().isEmpty()) {
            throw new IllegalArgumentException("Violation type cannot be empty.");
        }
        Violation violation = new Violation(detection, worker, safetyRule, violationType.trim(),
                severity != null ? severity : Violation.Severity.MEDIUM, description, confidence, imageUrl);
        return violationRepository.save(violation);
    }

    public Violation getViolationById(Long id) {
        return violationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Violation", id));
    }

    public List<Violation> getAllViolations() {
        return violationRepository.findAll();
    }

    public List<Violation> getOpenViolations() {
        return violationRepository.findOpenViolations();
    }

    public List<Violation> getViolationsBySeverity(Violation.Severity severity) {
        return violationRepository.findBySeverity(severity);
    }
}
