package com.siteguard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cameras")
public class Camera {

    public enum Status {
        ACTIVE,
        INACTIVE,
        ERROR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "camera_name", nullable = false, length = 100)
    private String cameraName;

    @Column(name = "camera_url", length = 500)
    private String cameraUrl;

    @Column(name = "location", length = 255)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private Status status = Status.ACTIVE;

    @Column(name = "last_active")
    private LocalDateTime lastActive;

    public Camera() {
        this.lastActive = LocalDateTime.now();
    }

    public Camera(Site site, String cameraName, String cameraUrl, String location, Status status) {
        this.site = site;
        this.cameraName = cameraName;
        this.cameraUrl = cameraUrl;
        this.location = location;
        this.status = status != null ? status : Status.ACTIVE;
        this.lastActive = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Site getSite() { return site; }
    public void setSite(Site site) { this.site = site; }

    public String getCameraName() { return cameraName; }
    public void setCameraName(String cameraName) { this.cameraName = cameraName; }

    public String getCameraUrl() { return cameraUrl; }
    public void setCameraUrl(String cameraUrl) { this.cameraUrl = cameraUrl; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getLastActive() { return lastActive; }
    public void setLastActive(LocalDateTime lastActive) { this.lastActive = lastActive; }

    @Override
    public String toString() {
        return "Camera{" +
                "id=" + id +
                ", cameraName='" + cameraName + '\'' +
                ", status=" + status +
                '}';
    }
}
