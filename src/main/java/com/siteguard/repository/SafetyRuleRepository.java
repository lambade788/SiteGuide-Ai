package com.siteguard.repository;

import com.siteguard.config.HibernateUtil;
import com.siteguard.entity.SafetyRule;
import com.siteguard.exception.DatabaseOperationException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class SafetyRuleRepository {

    private static final Logger logger = LoggerFactory.getLogger(SafetyRuleRepository.class);

    public SafetyRule save(SafetyRule rule) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            session.persist(rule);
            tx.commit();
            return rule;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            logger.error("Error saving safety rule", e);
            throw new DatabaseOperationException("Failed to save safety rule", e);
        }
    }

    public Optional<SafetyRule> findById(Long id) {
        try (Session session = HibernateUtil.openSession()) {
            return Optional.ofNullable(session.get(SafetyRule.class, id));
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch safety rule by ID: " + id, e);
        }
    }

    public Optional<SafetyRule> findByRuleType(String ruleType) {
        try (Session session = HibernateUtil.openSession()) {
            Query<SafetyRule> query = session.createQuery(
                    "FROM SafetyRule r WHERE r.ruleType = :rtype AND r.enabled = true", SafetyRule.class);
            query.setParameter("rtype", ruleType);
            List<SafetyRule> results = query.list();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch safety rule by type: " + ruleType, e);
        }
    }

    public List<SafetyRule> findAll() {
        try (Session session = HibernateUtil.openSession()) {
            return session.createQuery("FROM SafetyRule r ORDER BY r.id DESC", SafetyRule.class).list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch all safety rules", e);
        }
    }

    public SafetyRule update(SafetyRule rule) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            SafetyRule updated = session.merge(rule);
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DatabaseOperationException("Failed to update safety rule", e);
        }
    }

    public boolean deleteById(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            SafetyRule rule = session.get(SafetyRule.class, id);
            if (rule != null) {
                session.remove(rule);
                tx.commit();
                return true;
            }
            tx.commit();
            return false;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DatabaseOperationException("Failed to delete safety rule ID: " + id, e);
        }
    }
}
