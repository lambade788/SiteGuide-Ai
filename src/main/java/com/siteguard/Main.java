package com.siteguard;

import com.siteguard.ai.AiDetectionService;
import com.siteguard.ai.MockAiDetectionService;
import com.siteguard.config.HibernateUtil;
import com.siteguard.entity.Detection;
import com.siteguard.entity.Violation;
import com.siteguard.entity.Worker;
import com.siteguard.service.DetectionService;
import com.siteguard.service.ViolationService;
import com.siteguard.service.WorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   SITEGUARD AI - Construction Site Safety System ");
        System.out.println("   Technology: Java 17 | Hibernate 6 | MySQL | AI  ");
        System.out.println("==================================================");

        WorkerService workerService = new WorkerService();
        DetectionService detectionService = new DetectionService();
        ViolationService violationService = new ViolationService();
        AiDetectionService aiService = new MockAiDetectionService();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            System.out.print("Enter your choice (1-10): ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        handleAddWorker(scanner, workerService);
                        break;
                    case "2":
                        handleFindWorkerById(scanner, workerService);
                        break;
                    case "3":
                        handleListAllWorkers(workerService);
                        break;
                    case "4":
                        handleUpdateWorker(scanner, workerService);
                        break;
                    case "5":
                        handleDeleteWorker(scanner, workerService);
                        break;
                    case "6":
                        handleFindByEmployeeCode(scanner, workerService);
                        break;
                    case "7":
                        handleListActiveWorkers(workerService);
                        break;
                    case "8":
                        handleTestAiDetection(scanner, workerService, aiService);
                        break;
                    case "9":
                        handleViewDetectionsAndViolations(detectionService, violationService);
                        break;
                    case "10":
                        System.out.println("\nShutting down SiteGuard AI. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("\n[!] Invalid choice. Please enter a number between 1 and 10.");
                }
            } catch (Exception ex) {
                System.out.println("\n[ERROR] Operation failed: " + ex.getMessage());
                logger.error("CLI Operation Exception", ex);
            }
        }

        scanner.close();
        HibernateUtil.shutdown();
    }

    private static void printMenu() {
        System.out.println("\n==================================================");
        System.out.println("                   MAIN MENU                      ");
        System.out.println("==================================================");
        System.out.println(" 1. Add Worker (CREATE)");
        System.out.println(" 2. View Worker by ID (READ)");
        System.out.println(" 3. View All Workers (READ - HQL)");
        System.out.println(" 4. Update Worker (UPDATE)");
        System.out.println(" 5. Delete Worker (DELETE)");
        System.out.println(" 6. Find Worker by Employee Code (HQL Query)");
        System.out.println(" 7. Show Active Workers (HQL Query)");
        System.out.println(" 8. Test AI Safety Detection Simulation");
        System.out.println(" 9. View Detections & Safety Violations");
        System.out.println("10. Exit");
        System.out.println("==================================================");
    }

    private static void handleAddWorker(Scanner scanner, WorkerService service) {
        System.out.println("\n--- 1. ADD WORKER ---");
        System.out.print("Enter Worker Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Employee Code (e.g. W001): ");
        String empCode = scanner.nextLine();

        System.out.print("Enter Contractor Name (e.g. ABC Construction): ");
        String contractor = scanner.nextLine();

        Worker worker = service.createWorker(name, empCode, contractor, Worker.Status.ACTIVE);
        System.out.println("\n[SUCCESS] Worker created successfully!");
        System.out.println("ID: " + worker.getId() + " | Name: " + worker.getName()
                + " | Code: " + worker.getEmployeeCode() + " | Status: " + worker.getStatus());
    }

    private static void handleFindWorkerById(Scanner scanner, WorkerService service) {
        System.out.println("\n--- 2. VIEW WORKER BY ID ---");
        System.out.print("Enter Worker ID: ");
        Long id = Long.parseLong(scanner.nextLine().trim());

        Worker worker = service.getWorkerById(id);
        printWorkerDetails(worker);
    }

    private static void handleListAllWorkers(WorkerService service) {
        System.out.println("\n--- 3. ALL WORKERS ---");
        List<Worker> workers = service.getAllWorkers();
        if (workers.isEmpty()) {
            System.out.println("No workers found in database.");
        } else {
            workers.forEach(Main::printWorkerDetails);
        }
    }

    private static void handleUpdateWorker(Scanner scanner, WorkerService service) {
        System.out.println("\n--- 4. UPDATE WORKER ---");
        System.out.print("Enter Worker ID to update: ");
        Long id = Long.parseLong(scanner.nextLine().trim());

        Worker existing = service.getWorkerById(id);
        System.out.println("Current Details -> Name: " + existing.getName() + " | Contractor: "
                + existing.getContractor() + " | Status: " + existing.getStatus());

        System.out.print("Enter New Name (press Enter to keep '" + existing.getName() + "'): ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter New Contractor (press Enter to keep '" + existing.getContractor() + "'): ");
        String contractor = scanner.nextLine().trim();

        System.out.print("Enter New Status (ACTIVE / INACTIVE, press Enter to keep current): ");
        String statusStr = scanner.nextLine().trim();
        Worker.Status status = null;
        if (!statusStr.isEmpty()) {
            status = Worker.Status.valueOf(statusStr.toUpperCase());
        }

        Worker updated = service.updateWorker(id,
                name.isEmpty() ? null : name,
                contractor.isEmpty() ? null : contractor,
                status);
        System.out.println("\n[SUCCESS] Worker updated successfully!");
        printWorkerDetails(updated);
    }

    private static void handleDeleteWorker(Scanner scanner, WorkerService service) {
        System.out.println("\n--- 5. DELETE WORKER ---");
        System.out.print("Enter Worker ID to delete: ");
        Long id = Long.parseLong(scanner.nextLine().trim());

        service.deleteWorker(id);
        System.out.println("\n[SUCCESS] Worker ID " + id + " deleted successfully!");
    }

    private static void handleFindByEmployeeCode(Scanner scanner, WorkerService service) {
        System.out.println("\n--- 6. FIND BY EMPLOYEE CODE ---");
        System.out.print("Enter Employee Code (e.g. W001): ");
        String code = scanner.nextLine().trim();

        Worker worker = service.getWorkerByEmployeeCode(code);
        printWorkerDetails(worker);
    }

    private static void handleListActiveWorkers(WorkerService service) {
        System.out.println("\n--- 7. ACTIVE WORKERS ---");
        List<Worker> activeWorkers = service.getActiveWorkers();
        if (activeWorkers.isEmpty()) {
            System.out.println("No active workers found.");
        } else {
            activeWorkers.forEach(Main::printWorkerDetails);
        }
    }

    private static void handleTestAiDetection(Scanner scanner, WorkerService workerService, AiDetectionService aiService) {
        System.out.println("\n--- 8. AI SAFETY DETECTION SIMULATION ---");
        List<Worker> workers = workerService.getAllWorkers();
        Worker targetWorker = null;
        if (!workers.isEmpty()) {
            System.out.println("Select a worker for AI evaluation:");
            for (int i = 0; i < workers.size(); i++) {
                System.out.println(" (" + (i + 1) + ") " + workers.get(i).getName() + " [" + workers.get(i).getEmployeeCode() + "]");
            }
            System.out.print("Enter selection index (or press Enter for unassigned camera frame): ");
            String idxStr = scanner.nextLine().trim();
            if (!idxStr.isEmpty()) {
                int idx = Integer.parseInt(idxStr) - 1;
                if (idx >= 0 && idx < workers.size()) {
                    targetWorker = workers.get(idx);
                }
            }
        }

        System.out.println("\n[AI Vision Engine] Processing camera frame feed...");
        Violation violation = aiService.processAndRecordDetection(null, targetWorker, "/images/cam01_frame_0492.jpg");

        if (violation != null) {
            System.out.println("\n[!] SAFETY VIOLATION CONFIRMED & RECORDED [!]");
            System.out.println("Violation Type: " + violation.getViolationType());
            System.out.println("Severity      : " + violation.getSeverity());
            System.out.println("Confidence    : " + (violation.getConfidence() != null ? violation.getConfidence() : "N/A"));
            System.out.println("Description   : " + violation.getDescription());
            System.out.println("Status        : " + violation.getStatus());
        } else {
            System.out.println("\n[OK] AI Detection: Full PPE compliance verified. No violation created.");
        }
    }

    private static void handleViewDetectionsAndViolations(DetectionService detectionService, ViolationService violationService) {
        System.out.println("\n--- 9. DETECTIONS & SAFETY VIOLATIONS RECORD ---");
        List<Detection> detections = detectionService.getAllDetections();
        System.out.println("\n>>> Raw AI Detections Count: " + detections.size());
        detections.forEach(d -> System.out.println("  - [Detection #" + d.getId() + "] Type: " + d.getObjectType()
                + " | Confidence: " + d.getConfidence() + " | Time: " + d.getDetectedAt()));

        List<Violation> violations = violationService.getAllViolations();
        System.out.println("\n>>> Confirmed Safety Violations Count: " + violations.size());
        violations.forEach(v -> System.out.println("  - [Violation #" + v.getId() + "] Type: " + v.getViolationType()
                + " | Severity: " + v.getSeverity() + " | Status: " + v.getStatus()
                + " | Worker: " + (v.getWorker() != null ? v.getWorker().getName() : "Unassigned")));
    }

    private static void printWorkerDetails(Worker worker) {
        System.out.println(String.format("  [Worker #%d] Name: %-18s | Code: %-8s | Contractor: %-16s | Status: %s",
                worker.getId(), worker.getName(), worker.getEmployeeCode(), worker.getContractor(), worker.getStatus()));
    }
}
