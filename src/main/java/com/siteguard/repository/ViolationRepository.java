package com.siteguard.repository;

import com.siteguard.config.HibernateUtil;
import com.siteguard.entity.Violation;
import com.siteguard.exception.DatabaseOperationException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ViolationRepository {

    private static final Logger logger = LoggerFactory.getLogger(ViolationRepository.class);

    public Violation save(Violation violation) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            session.persist(violation);
            tx.commit();
            logger.info("Saved violation ID: {} type: {}", violation.getId(), violation.getViolationType());
            return violation;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            logger.error("Error saving violation", e);
            throw new DatabaseOperationException("Failed to save violation", e);
        }
    }

    public Optional<Violation> findById(Long id) {
        try (Session session = HibernateUtil.openSession()) {
            return Optional.ofNullable(session.get(Violation.class, id));
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch violation by ID: " + id, e);
        }
    }

    public List<Violation> findAll() {
        try (Session session = HibernateUtil.openSession()) {
            return session.createQuery("FROM Violation v ORDER BY v.detectedAt DESC", Violation.class).list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch all violations", e);
        }
    }

    public List<Violation> findOpenViolations() {
        try (Session session = HibernateUtil.openSession()) {
            Query<Violation> query = session.createQuery(
                    "FROM Violation v WHERE v.status = :status ORDER BY v.detectedAt DESC", Violation.class);
            query.setParameter("status", Violation.Status.OPEN);
            return query.list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch open violations", e);
        }
    }

    public List<Violation> findBySeverity(Violation.Severity severity) {
        try (Session session = HibernateUtil.openSession()) {
            Query<Violation> query = session.createQuery(
                    "FROM Violation v WHERE v.severity = :sev ORDER BY v.detectedAt DESC", Violation.class);
            query.setParameter("sev", severity);
            return query.list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch violations by severity", e);
        }
    }

    public Violation update(Violation violation) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            Violation updated = session.merge(violation);
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DatabaseOperationException("Failed to update violation", e);
        }
    }
}
