package com.siteguard.service;

import com.siteguard.entity.Worker;
import com.siteguard.exception.EntityNotFoundException;
import com.siteguard.repository.AuditLogRepository;
import com.siteguard.repository.WorkerRepository;
import com.siteguard.entity.AuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WorkerService {

    private static final Logger logger = LoggerFactory.getLogger(WorkerService.class);
    private final WorkerRepository workerRepository;
    private final AuditLogRepository auditLogRepository;

    public WorkerService() {
        this.workerRepository = new WorkerRepository();
        this.auditLogRepository = new AuditLogRepository();
    }

    public WorkerService(WorkerRepository workerRepository, AuditLogRepository auditLogRepository) {
        this.workerRepository = workerRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public Worker createWorker(String name, String employeeCode, String contractor, Worker.Status status) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Worker name cannot be empty.");
        }
        if (employeeCode != null && !employeeCode.trim().isEmpty()) {
            workerRepository.findByEmployeeCode(employeeCode.trim()).ifPresent(w -> {
                throw new IllegalArgumentException("Employee code already exists: " + employeeCode);
            });
        }

        Worker worker = new Worker(name.trim(),
                employeeCode != null ? employeeCode.trim() : null,
                contractor != null ? contractor.trim() : null,
                status != null ? status : Worker.Status.ACTIVE);

        Worker saved = workerRepository.save(worker);
        auditLogRepository.save(new AuditLog("CREATE_WORKER", "Worker", saved.getId(),
                "Created worker: " + saved.getName() + " (" + saved.getEmployeeCode() + ")"));
        return saved;
    }

    public Worker getWorkerById(Long id) {
        return workerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Worker", id));
    }

    public List<Worker> getAllWorkers() {
        return workerRepository.findAll();
    }

    public Worker getWorkerByEmployeeCode(String employeeCode) {
        if (employeeCode == null || employeeCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee code cannot be empty.");
        }
        return workerRepository.findByEmployeeCode(employeeCode.trim())
                .orElseThrow(() -> new EntityNotFoundException("Worker with employee code '" + employeeCode + "' not found."));
    }

    public List<Worker> getActiveWorkers() {
        return workerRepository.findActiveWorkers();
    }

    public Worker updateWorker(Long id, String name, String contractor, Worker.Status status) {
        Worker worker = getWorkerById(id);
        if (name != null && !name.trim().isEmpty()) {
            worker.setName(name.trim());
        }
        if (contractor != null) {
            worker.setContractor(contractor.trim());
        }
        if (status != null) {
            worker.setStatus(status);
        }

        Worker updated = workerRepository.update(worker);
        auditLogRepository.save(new AuditLog("UPDATE_WORKER", "Worker", updated.getId(),
                "Updated worker ID: " + updated.getId()));
        return updated;
    }

    public void deleteWorker(Long id) {
        Worker worker = getWorkerById(id);
        workerRepository.deleteById(id);
        auditLogRepository.save(new AuditLog("DELETE_WORKER", "Worker", id,
                "Deleted worker: " + worker.getName()));
    }
}
