package com.siteguard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workers")
public class Worker {

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = true)
    private Project project;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "employee_code", unique = true, length = 80)
    private String employeeCode;

    @Column(name = "contractor", length = 150)
    private String contractor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private Status status = Status.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Worker() {
        this.createdAt = LocalDateTime.now();
    }

    public Worker(String name, String employeeCode, String contractor, Status status) {
        this.name = name;
        this.employeeCode = employeeCode;
        this.contractor = contractor;
        this.status = status != null ? status : Status.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public Worker(Project project, String name, String employeeCode, String contractor, Status status) {
        this.project = project;
        this.name = name;
        this.employeeCode = employeeCode;
        this.contractor = contractor;
        this.status = status != null ? status : Status.ACTIVE;
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

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getContractor() { return contractor; }
    public void setContractor(String contractor) { this.contractor = contractor; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Worker{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", employeeCode='" + employeeCode + '\'' +
                ", contractor='" + contractor + '\'' +
                ", status=" + status +
                '}';
    }
}
