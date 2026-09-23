package com.siteguard.ai;

import com.siteguard.entity.Camera;
import com.siteguard.entity.Detection;
import com.siteguard.entity.Violation;
import com.siteguard.entity.Worker;

public interface AiDetectionService {

    AiDetectionResult analyzeFrame(Camera camera, String imagePath);

    AiDetectionResult processWorkerFrame(Worker worker, String imagePath);

    Violation processAndRecordDetection(Camera camera, Worker worker, String imagePath);
}
