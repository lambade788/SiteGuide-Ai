package com.siteguard.service;

import com.siteguard.entity.SafetyRule;
import com.siteguard.exception.EntityNotFoundException;
import com.siteguard.repository.SafetyRuleRepository;

import java.util.List;
import java.util.Optional;

public class SafetyRuleService {

    private final SafetyRuleRepository safetyRuleRepository;

    public SafetyRuleService() {
        this.safetyRuleRepository = new SafetyRuleRepository();
    }

    public SafetyRuleService(SafetyRuleRepository safetyRuleRepository) {
        this.safetyRuleRepository = safetyRuleRepository;
    }

    public SafetyRule createSafetyRule(String ruleName, String ruleType, SafetyRule.Severity severity, String description, Boolean enabled) {
        if (ruleName == null || ruleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule name cannot be empty.");
        }
        if (ruleType == null || ruleType.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule type cannot be empty.");
        }
        SafetyRule rule = new SafetyRule(ruleName.trim(), ruleType.trim(), severity, description, enabled);
        return safetyRuleRepository.save(rule);
    }

    public SafetyRule getSafetyRuleById(Long id) {
        return safetyRuleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SafetyRule", id));
    }

    public Optional<SafetyRule> getSafetyRuleByType(String ruleType) {
        return safetyRuleRepository.findByRuleType(ruleType);
    }

    public List<SafetyRule> getAllSafetyRules() {
        return safetyRuleRepository.findAll();
    }
}
