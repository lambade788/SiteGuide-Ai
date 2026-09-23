package com.siteguard.ai;

import com.siteguard.entity.*;
import com.siteguard.service.AlertService;
import com.siteguard.service.DetectionService;
import com.siteguard.service.SafetyRuleService;
import com.siteguard.service.ViolationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MockAiDetectionService implements AiDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(MockAiDetectionService.class);
    private final DetectionService detectionService;
    private final ViolationService violationService;
    private final AlertService alertService;
    private final SafetyRuleService safetyRuleService;
    private final Random random = new Random();

    public MockAiDetectionService() {
        this.detectionService = new DetectionService();
        this.violationService = new ViolationService();
        this.alertService = new AlertService();
        this.safetyRuleService = new SafetyRuleService();
    }

    public MockAiDetectionService(DetectionService detectionService,
                                 ViolationService violationService,
                                 AlertService alertService,
                                 SafetyRuleService safetyRuleService) {
        this.detectionService = detectionService;
        this.violationService = violationService;
        this.alertService = alertService;
        this.safetyRuleService = safetyRuleService;
    }

    @Override
    public AiDetectionResult analyzeFrame(Camera camera, String imagePath) {
        return generateMockResult("CAMERA_FRAME", imagePath);
    }

    @Override
    public AiDetectionResult processWorkerFrame(Worker worker, String imagePath) {
        String workerName = worker != null ? worker.getName() : "Unknown Worker";
        return generateMockResult(workerName, imagePath);
    }

    @Override
    public Violation processAndRecordDetection(Camera camera, Worker worker, String imagePath) {
        AiDetectionResult result = processWorkerFrame(worker, imagePath);

        // 1. Record raw AI detection in database
        Detection detection = detectionService.recordDetection(
                camera,
                worker,
                result.getObjectType(),
                result.getConfidence(),
                imagePath,
                "{" +
                        "\"x\":" + result.getX() + "," +
                        "\"y\":" + result.getY() + "," +
                        "\"w\":" + result.getWidth() + "," +
                        "\"h\":" + result.getHeight() +
                "}"
        );

        if (!result.isViolation()) {
            logger.info("AI Analysis: No safety violation detected for worker/frame.");
            return null;
        }

        // 2. Lookup or create matching Safety Rule
        SafetyRule rule = safetyRuleService.getSafetyRuleByType(result.getObjectType())
                .orElseGet(() -> safetyRuleService.createSafetyRule(
                        result.getObjectType() + " RULE",
                        result.getObjectType(),
                        result.getSeverity(),
                        "Mandatory compliance rule for " + result.getObjectType(),
                        true
                ));

        // 3. Convert detection into confirmed Safety Violation
        Violation.Severity violationSeverity = convertSeverity(result.getSeverity());
        Violation violation = violationService.createViolation(
                detection,
                worker,
                rule,
                result.getObjectType(),
                violationSeverity,
                result.getDescription(),
                result.getConfidence(),
                imagePath
        );

        // 4. Generate Alert for HIGH/CRITICAL violations
        if (violationSeverity == Violation.Severity.HIGH || violationSeverity == Violation.Severity.CRITICAL) {
            String alertMsg = "SAFETY ALERT: " + result.getObjectType() + " detected. Worker: "
                    + (worker != null ? worker.getName() + " (" + worker.getEmployeeCode() + ")" : "Unidentified")
                    + ". Confidence: " + result.getConfidence();
            alertService.createAlert(
                    violation,
                    alertMsg,
                    convertAlertSeverity(violationSeverity),
                    "AI_SAFETY_VIOLATION"
            );
        }

        return violation;
    }

    private AiDetectionResult generateMockResult(String context, String imagePath) {
        int scenario = random.nextInt(4);
        switch (scenario) {
            case 0:
                return new AiDetectionResult(
                        "HELMET_MISSING",
                        BigDecimal.valueOf(0.92 + (random.nextDouble() * 0.07)).setScale(4, RoundingMode.HALF_UP),
                        120, 85, 200, 240,
                        SafetyRule.Severity.HIGH,
                        "Worker observed operating without mandatory hard hat/safety helmet.",
                        true
                );
            case 1:
                return new AiDetectionResult(
                        "VEST_MISSING",
                        BigDecimal.valueOf(0.89 + (random.nextDouble() * 0.09)).setScale(4, RoundingMode.HALF_UP),
                        140, 190, 220, 310,
                        SafetyRule.Severity.MEDIUM,
                        "Worker observed without high-visibility safety vest.",
                        true
                );
            case 2:
                return new AiDetectionResult(
                        "RESTRICTED_AREA_ENTRY",
                        BigDecimal.valueOf(0.95 + (random.nextDouble() * 0.04)).setScale(4, RoundingMode.HALF_UP),
                        50, 40, 400, 500,
                        SafetyRule.Severity.CRITICAL,
                        "Unauthorized person detected inside heavy machinery operation zone.",
                        true
                );
            default:
                return new AiDetectionResult(
                        "FULL_PPE_COMPLIANT",
                        BigDecimal.valueOf(0.97 + (random.nextDouble() * 0.02)).setScale(4, RoundingMode.HALF_UP),
                        100, 100, 150, 300,
                        SafetyRule.Severity.LOW,
                        "Full PPE compliance verified (Helmet, Vest, Shoes).",
                        false
                );
        }
    }

    private Violation.Severity convertSeverity(SafetyRule.Severity severity) {
        if (severity == null) return Violation.Severity.MEDIUM;
        switch (severity) {
            case LOW: return Violation.Severity.LOW;
            case HIGH: return Violation.Severity.HIGH;
            case CRITICAL: return Violation.Severity.CRITICAL;
            default: return Violation.Severity.MEDIUM;
        }
    }

    private Alert.Severity convertAlertSeverity(Violation.Severity severity) {
        if (severity == null) return Alert.Severity.MEDIUM;
        switch (severity) {
            case LOW: return Alert.Severity.LOW;
            case HIGH: return Alert.Severity.HIGH;
            case CRITICAL: return Alert.Severity.CRITICAL;
            default: return Alert.Severity.MEDIUM;
        }
    }
}
