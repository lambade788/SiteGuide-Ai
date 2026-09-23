package com.siteguard.service;

import com.siteguard.entity.Alert;
import com.siteguard.entity.Violation;
import com.siteguard.exception.EntityNotFoundException;
import com.siteguard.repository.AlertRepository;

import java.util.List;

public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService() {
        this.alertRepository = new AlertRepository();
    }

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public Alert createAlert(Violation violation, String message, Alert.Severity severity, String alertType) {
        if (violation == null) {
            throw new IllegalArgumentException("Violation cannot be null when generating an alert.");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Alert message cannot be empty.");
        }
        Alert alert = new Alert(violation, message.trim(),
                severity != null ? severity : Alert.Severity.HIGH,
                alertType != null ? alertType : "SAFETY_VIOLATION");
        return alertRepository.save(alert);
    }

    public Alert getAlertById(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alert", id));
    }

    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public List<Alert> getUnreadAlerts() {
        return alertRepository.findUnreadAlerts();
    }
}
