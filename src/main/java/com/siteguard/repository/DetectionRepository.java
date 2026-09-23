package com.siteguard.repository;

import com.siteguard.config.HibernateUtil;
import com.siteguard.entity.Detection;
import com.siteguard.exception.DatabaseOperationException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class DetectionRepository {

    private static final Logger logger = LoggerFactory.getLogger(DetectionRepository.class);

    public Detection save(Detection detection) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            session.persist(detection);
            tx.commit();
            logger.info("Saved AI detection ID: {}", detection.getId());
            return detection;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            logger.error("Error saving detection", e);
            throw new DatabaseOperationException("Failed to save detection", e);
        }
    }

    public Optional<Detection> findById(Long id) {
        try (Session session = HibernateUtil.openSession()) {
            return Optional.ofNullable(session.get(Detection.class, id));
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch detection by ID: " + id, e);
        }
    }

    public List<Detection> findAll() {
        try (Session session = HibernateUtil.openSession()) {
            return session.createQuery("FROM Detection d ORDER BY d.detectedAt DESC", Detection.class).list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch detections", e);
        }
    }

    public List<Detection> findByCameraId(Long cameraId) {
        try (Session session = HibernateUtil.openSession()) {
            Query<Detection> query = session.createQuery(
                    "FROM Detection d WHERE d.camera.id = :cid ORDER BY d.detectedAt DESC", Detection.class);
            query.setParameter("cid", cameraId);
            return query.list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch detections for camera ID: " + cameraId, e);
        }
    }

    public List<Detection> findByWorkerId(Long workerId) {
        try (Session session = HibernateUtil.openSession()) {
            Query<Detection> query = session.createQuery(
                    "FROM Detection d WHERE d.worker.id = :wid ORDER BY d.detectedAt DESC", Detection.class);
            query.setParameter("wid", workerId);
            return query.list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch detections for worker ID: " + workerId, e);
        }
    }
}
