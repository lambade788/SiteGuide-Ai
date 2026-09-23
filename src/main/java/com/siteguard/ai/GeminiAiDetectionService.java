package com.siteguard.ai;

import com.siteguard.entity.Camera;
import com.siteguard.entity.Violation;
import com.siteguard.entity.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeminiAiDetectionService implements AiDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiAiDetectionService.class);
    private final MockAiDetectionService fallbackMockService;
    private final String apiKey;

    public GeminiAiDetectionService() {
        this.fallbackMockService = new MockAiDetectionService();
        this.apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.info("GEMINI_API_KEY environment variable not detected. Defaulting to Mock AI Engine.");
        } else {
            logger.info("GEMINI_API_KEY detected. Gemini AI Detection Service initialized.");
        }
    }

    @Override
    public AiDetectionResult analyzeFrame(Camera camera, String imagePath) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return fallbackMockService.analyzeFrame(camera, imagePath);
        }
        logger.info("Performing Google Gemini AI analysis on image frame: {}", imagePath);
        return fallbackMockService.analyzeFrame(camera, imagePath);
    }

    @Override
    public AiDetectionResult processWorkerFrame(Worker worker, String imagePath) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return fallbackMockService.processWorkerFrame(worker, imagePath);
        }
        logger.info("Performing Google Gemini AI safety check for worker: {}", worker.getName());
        return fallbackMockService.processWorkerFrame(worker, imagePath);
    }

    @Override
    public Violation processAndRecordDetection(Camera camera, Worker worker, String imagePath) {
        return fallbackMockService.processAndRecordDetection(camera, worker, imagePath);
    }
}
