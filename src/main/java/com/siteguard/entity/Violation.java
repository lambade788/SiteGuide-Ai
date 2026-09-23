package com.siteguard.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "violations")
public class Violation {

    public enum Severity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    public enum Status {
        OPEN,
        ACKNOWLEDGED,
        RESOLVED,
        FALSE_POSITIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id", nullable = true)
    private Site site;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "camera_id", nullable = true)
    private Camera camera;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "detection_id", nullable = true)
    private Detection detection;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "worker_id", nullable = true)
    private Worker worker;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "safety_rule_id", nullable = true)
    private SafetyRule safetyRule;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resolved_by", nullable = true)
    private User resolvedBy;

    @Column(name = "violation_type", nullable = false, length = 100)
    private String violationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 50)
    private Severity severity;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "confidence", precision = 5, scale = 4)
    private BigDecimal confidence;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private Status status = Status.OPEN;

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    public Violation() {
        this.detectedAt = LocalDateTime.now();
    }

    public Violation(Detection detection, Worker worker, SafetyRule safetyRule, String violationType, Severity severity, String description, BigDecimal confidence, String imageUrl) {
        this.detection = detection;
        this.worker = worker;
        this.safetyRule = safetyRule;
        this.violationType = violationType;
        this.severity = severity;
        this.description = description;
        this.confidence = confidence;
        this.imageUrl = imageUrl;
        this.status = Status.OPEN;
        this.detectedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (detectedAt == null) {
            detectedAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Site getSite() { return site; }
    public void setSite(Site site) { this.site = site; }

    public Camera getCamera() { return camera; }
    public void setCamera(Camera camera) { this.camera = camera; }

    public Detection getDetection() { return detection; }
    public void setDetection(Detection detection) { this.detection = detection; }

    public Worker getWorker() { return worker; }
    public void setWorker(Worker worker) { this.worker = worker; }

    public SafetyRule getSafetyRule() { return safetyRule; }
    public void setSafetyRule(SafetyRule safetyRule) { this.safetyRule = safetyRule; }

    public User getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(User resolvedBy) { this.resolvedBy = resolvedBy; }

    public String getViolationType() { return violationType; }
    public void setViolationType(String violationType) { this.violationType = violationType; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    @Override
    public String toString() {
        return "Violation{" +
                "id=" + id +
                ", violationType='" + violationType + '\'' +
                ", severity=" + severity +
                ", status=" + status +
                ", detectedAt=" + detectedAt +
                '}';
    }
}
