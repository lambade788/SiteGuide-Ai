package com.siteguard.repository;

import com.siteguard.config.HibernateUtil;
import com.siteguard.entity.Alert;
import com.siteguard.exception.DatabaseOperationException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class AlertRepository {

    private static final Logger logger = LoggerFactory.getLogger(AlertRepository.class);

    public Alert save(Alert alert) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            session.persist(alert);
            tx.commit();
            logger.info("Saved alert ID: {}", alert.getId());
            return alert;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            logger.error("Error saving alert", e);
            throw new DatabaseOperationException("Failed to save alert", e);
        }
    }

    public Optional<Alert> findById(Long id) {
        try (Session session = HibernateUtil.openSession()) {
            return Optional.ofNullable(session.get(Alert.class, id));
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch alert by ID: " + id, e);
        }
    }

    public List<Alert> findAll() {
        try (Session session = HibernateUtil.openSession()) {
            return session.createQuery("FROM Alert a ORDER BY a.createdAt DESC", Alert.class).list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch all alerts", e);
        }
    }

    public List<Alert> findUnreadAlerts() {
        try (Session session = HibernateUtil.openSession()) {
            Query<Alert> query = session.createQuery(
                    "FROM Alert a WHERE a.isRead = false ORDER BY a.createdAt DESC", Alert.class);
            return query.list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch unread alerts", e);
        }
    }

    public Alert update(Alert alert) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            Alert updated = session.merge(alert);
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DatabaseOperationException("Failed to update alert", e);
        }
    }
}
