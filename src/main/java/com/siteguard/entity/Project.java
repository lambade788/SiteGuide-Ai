package com.siteguard.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
public class Project {

    public enum Status {
        PLANNED,
        ACTIVE,
        COMPLETED,
        ON_HOLD
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "client_name", length = 150)
    private String clientName;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "expected_end")
    private LocalDate expectedEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private Status status = Status.PLANNED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Project() {
        this.createdAt = LocalDateTime.now();
    }

    public Project(String name, String description, String clientName, LocalDate startDate, LocalDate expectedEnd, Status status) {
        this.name = name;
        this.description = description;
        this.clientName = clientName;
        this.startDate = startDate;
        this.expectedEnd = expectedEnd;
        this.status = status != null ? status : Status.PLANNED;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getExpectedEnd() { return expectedEnd; }
    public void setExpectedEnd(LocalDate expectedEnd) { this.expectedEnd = expectedEnd; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", clientName='" + clientName + '\'' +
                ", status=" + status +
                '}';
    }
}
