package com.siteguard.ai;

import com.siteguard.entity.SafetyRule;

import java.math.BigDecimal;

public class AiDetectionResult {

    private final String objectType;
    private final BigDecimal confidence;
    private final Integer x;
    private final Integer y;
    private final Integer width;
    private final Integer height;
    private final SafetyRule.Severity severity;
    private final String description;
    private final boolean isViolation;

    public AiDetectionResult(String objectType, BigDecimal confidence, Integer x, Integer y,
                              Integer width, Integer height, SafetyRule.Severity severity,
                              String description, boolean isViolation) {
        this.objectType = objectType;
        this.confidence = confidence;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.severity = severity;
        this.description = description;
        this.isViolation = isViolation;
    }

    public String getObjectType() { return objectType; }
    public BigDecimal getConfidence() { return confidence; }
    public Integer getX() { return x; }
    public Integer getY() { return y; }
    public Integer getWidth() { return width; }
    public Integer getHeight() { return height; }
    public SafetyRule.Severity getSeverity() { return severity; }
    public String getDescription() { return description; }
    public boolean isViolation() { return isViolation; }

    @Override
    public String toString() {
        return "AiDetectionResult{" +
                "objectType='" + objectType + '\'' +
                ", confidence=" + confidence +
                ", severity=" + severity +
                ", isViolation=" + isViolation +
                ", description='" + description + '\'' +
                '}';
    }
}
