package com.siteguard.repository;

import com.siteguard.config.HibernateUtil;
import com.siteguard.entity.Camera;
import com.siteguard.exception.DatabaseOperationException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class CameraRepository {

    private static final Logger logger = LoggerFactory.getLogger(CameraRepository.class);

    public Camera save(Camera camera) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            session.persist(camera);
            tx.commit();
            return camera;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            logger.error("Error saving camera", e);
            throw new DatabaseOperationException("Failed to save camera", e);
        }
    }

    public Optional<Camera> findById(Long id) {
        try (Session session = HibernateUtil.openSession()) {
            return Optional.ofNullable(session.get(Camera.class, id));
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch camera by ID: " + id, e);
        }
    }

    public List<Camera> findAll() {
        try (Session session = HibernateUtil.openSession()) {
            return session.createQuery("FROM Camera c ORDER BY c.id DESC", Camera.class).list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch all cameras", e);
        }
    }

    public List<Camera> findBySiteId(Long siteId) {
        try (Session session = HibernateUtil.openSession()) {
            Query<Camera> query = session.createQuery("FROM Camera c WHERE c.site.id = :sid", Camera.class);
            query.setParameter("sid", siteId);
            return query.list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch cameras for site ID: " + siteId, e);
        }
    }

    public Camera update(Camera camera) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            Camera updated = session.merge(camera);
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DatabaseOperationException("Failed to update camera", e);
        }
    }

    public boolean deleteById(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            Camera camera = session.get(Camera.class, id);
            if (camera != null) {
                session.remove(camera);
                tx.commit();
                return true;
            }
            tx.commit();
            return false;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DatabaseOperationException("Failed to delete camera ID: " + id, e);
        }
    }
}
