package com.siteguard;

import com.siteguard.entity.Worker;
import com.siteguard.exception.EntityNotFoundException;
import com.siteguard.repository.AuditLogRepository;
import com.siteguard.repository.WorkerRepository;
import com.siteguard.service.WorkerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class WorkerServiceTest {

    private WorkerRepository workerRepository;
    private AuditLogRepository auditLogRepository;
    private WorkerService workerService;

    @BeforeEach
    void setUp() {
        // Test stub repositories without requiring active DB connection during offline unit test
        workerRepository = new WorkerRepository() {
            private Worker storedWorker;

            @Override
            public Worker save(Worker worker) {
                worker.setId(101L);
                this.storedWorker = worker;
                return worker;
            }

            @Override
            public Optional<Worker> findById(Long id) {
                if (storedWorker != null && storedWorker.getId().equals(id)) {
                    return Optional.of(storedWorker);
                }
                return Optional.empty();
            }

            @Override
            public Optional<Worker> findByEmployeeCode(String employeeCode) {
                if (storedWorker != null && employeeCode.equals(storedWorker.getEmployeeCode())) {
                    return Optional.of(storedWorker);
                }
                return Optional.empty();
            }

            @Override
            public Worker update(Worker worker) {
                this.storedWorker = worker;
                return worker;
            }

            @Override
            public boolean deleteById(Long id) {
                if (storedWorker != null && storedWorker.getId().equals(id)) {
                    storedWorker = null;
                    return true;
                }
                return false;
            }
        };

        auditLogRepository = new AuditLogRepository() {
            @Override
            public com.siteguard.entity.AuditLog save(com.siteguard.entity.AuditLog log) {
                return log;
            }
        };

        workerService = new WorkerService(workerRepository, auditLogRepository);
    }

    @Test
    void testCreateWorkerSuccess() {
        Worker worker = workerService.createWorker("Rahul Sharma", "W001", "ABC Construction", Worker.Status.ACTIVE);
        assertNotNull(worker.getId());
        assertEquals("Rahul Sharma", worker.getName());
        assertEquals("W001", worker.getEmployeeCode());
        assertEquals(Worker.Status.ACTIVE, worker.getStatus());
    }

    @Test
    void testCreateWorkerEmptyNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            workerService.createWorker("   ", "W002", "Contractor", Worker.Status.ACTIVE);
        });
    }

    @Test
    void testDuplicateEmployeeCodeThrowsException() {
        workerService.createWorker("Worker 1", "W001", "Contractor", Worker.Status.ACTIVE);
        assertThrows(IllegalArgumentException.class, () -> {
            workerService.createWorker("Worker 2", "W001", "Contractor", Worker.Status.ACTIVE);
        });
    }

    @Test
    void testGetWorkerByIdNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            workerService.getWorkerById(999L);
        });
    }
}
