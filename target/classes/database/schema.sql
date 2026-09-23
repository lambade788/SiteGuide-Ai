-- =============================================================================
-- SiteGuard AI - Database Schema DDL Reference Script
-- Target Database: MySQL 8.x
-- =============================================================================

CREATE DATABASE IF NOT EXISTS siteguard_ai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE siteguard_ai;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'SITE_MANAGER', 'SAFETY_OFFICER', 'VIEWER') NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Projects Table
CREATE TABLE IF NOT EXISTS projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    client_name VARCHAR(150),
    start_date DATE,
    expected_end DATE,
    status ENUM('PLANNED', 'ACTIVE', 'COMPLETED', 'ON_HOLD') NOT NULL DEFAULT 'PLANNED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Sites Table
CREATE TABLE IF NOT EXISTS sites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    location VARCHAR(255),
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sites_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Cameras Table
CREATE TABLE IF NOT EXISTS cameras (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    site_id BIGINT NOT NULL,
    camera_name VARCHAR(100) NOT NULL,
    camera_url VARCHAR(500),
    location VARCHAR(255),
    status ENUM('ACTIVE', 'INACTIVE', 'ERROR') NOT NULL DEFAULT 'ACTIVE',
    last_active DATETIME DEFAULT NULL,
    CONSTRAINT fk_cameras_site FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Workers Table
CREATE TABLE IF NOT EXISTS workers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT DEFAULT NULL,
    name VARCHAR(120) NOT NULL,
    employee_code VARCHAR(80) UNIQUE,
    contractor VARCHAR(150),
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_workers_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Safety Rules Table
CREATE TABLE IF NOT EXISTS safety_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT DEFAULT NULL,
    rule_name VARCHAR(150) NOT NULL,
    rule_type VARCHAR(80) NOT NULL,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,
    description TEXT,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_safety_rules_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. AI Detections Table
CREATE TABLE IF NOT EXISTS detections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    camera_id BIGINT DEFAULT NULL,
    worker_id BIGINT DEFAULT NULL,
    object_type VARCHAR(80) NOT NULL,
    confidence DECIMAL(5, 4) NOT NULL,
    x INT DEFAULT NULL,
    y INT DEFAULT NULL,
    width INT DEFAULT NULL,
    height INT DEFAULT NULL,
    image_url VARCHAR(500) DEFAULT NULL,
    metadata VARCHAR(500) DEFAULT NULL,
    detected_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_detections_camera FOREIGN KEY (camera_id) REFERENCES cameras(id) ON DELETE SET NULL,
    CONSTRAINT fk_detections_worker FOREIGN KEY (worker_id) REFERENCES workers(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Violations Table
CREATE TABLE IF NOT EXISTS violations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    site_id BIGINT DEFAULT NULL,
    camera_id BIGINT DEFAULT NULL,
    detection_id BIGINT DEFAULT NULL,
    worker_id BIGINT DEFAULT NULL,
    safety_rule_id BIGINT DEFAULT NULL,
    resolved_by BIGINT DEFAULT NULL,
    violation_type VARCHAR(100) NOT NULL,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,
    description TEXT,
    confidence DECIMAL(5, 4) DEFAULT NULL,
    image_url VARCHAR(500) DEFAULT NULL,
    status ENUM('OPEN', 'ACKNOWLEDGED', 'RESOLVED', 'FALSE_POSITIVE') NOT NULL DEFAULT 'OPEN',
    detected_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at DATETIME DEFAULT NULL,
    CONSTRAINT fk_violations_site FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE SET NULL,
    CONSTRAINT fk_violations_camera FOREIGN KEY (camera_id) REFERENCES cameras(id) ON DELETE SET NULL,
    CONSTRAINT fk_violations_detection FOREIGN KEY (detection_id) REFERENCES detections(id) ON DELETE SET NULL,
    CONSTRAINT fk_violations_worker FOREIGN KEY (worker_id) REFERENCES workers(id) ON DELETE SET NULL,
    CONSTRAINT fk_violations_rule FOREIGN KEY (safety_rule_id) REFERENCES safety_rules(id) ON DELETE SET NULL,
    CONSTRAINT fk_violations_user FOREIGN KEY (resolved_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Alerts Table
CREATE TABLE IF NOT EXISTS alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    violation_id BIGINT NOT NULL,
    user_id BIGINT DEFAULT NULL,
    message TEXT NOT NULL,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,
    alert_type VARCHAR(50) NOT NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at DATETIME DEFAULT NULL,
    CONSTRAINT fk_alerts_violation FOREIGN KEY (violation_id) REFERENCES violations(id) ON DELETE CASCADE,
    CONSTRAINT fk_alerts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. Audit Logs Table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT DEFAULT NULL,
    action VARCHAR(80) NOT NULL,
    entity_type VARCHAR(80) NOT NULL,
    entity_id BIGINT DEFAULT NULL,
    description TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
