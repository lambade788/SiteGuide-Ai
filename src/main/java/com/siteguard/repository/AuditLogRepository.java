package com.siteguard.repository;

import com.siteguard.config.HibernateUtil;
import com.siteguard.entity.AuditLog;
import com.siteguard.exception.DatabaseOperationException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AuditLogRepository {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogRepository.class);

    public AuditLog save(AuditLog log) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            session.persist(log);
            tx.commit();
            return log;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            logger.error("Error saving audit log action: {}", log.getAction(), e);
            throw new DatabaseOperationException("Failed to save audit log", e);
        }
    }

    public List<AuditLog> findAll() {
        try (Session session = HibernateUtil.openSession()) {
            return session.createQuery("FROM AuditLog a ORDER BY a.createdAt DESC", AuditLog.class).list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch audit logs", e);
        }
    }
}
