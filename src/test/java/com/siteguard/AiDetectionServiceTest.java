package com.siteguard;

import com.siteguard.ai.AiDetectionResult;
import com.siteguard.ai.MockAiDetectionService;
import com.siteguard.entity.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiDetectionServiceTest {

    @Test
    void testMockAiDetectionReturnsValidResult() {
        MockAiDetectionService mockAi = new MockAiDetectionService(null, null, null, null);
        Worker worker = new Worker("Amit Kumar", "W005", "XYZ Infra", Worker.Status.ACTIVE);

        AiDetectionResult result = mockAi.processWorkerFrame(worker, "/images/test_frame.jpg");

        assertNotNull(result);
        assertNotNull(result.getObjectType());
        assertNotNull(result.getConfidence());
        assertTrue(result.getConfidence().doubleValue() >= 0.0 && result.getConfidence().doubleValue() <= 1.0);
    }
}
