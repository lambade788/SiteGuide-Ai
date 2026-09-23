package com.siteguard.repository;

import com.siteguard.config.HibernateUtil;
import com.siteguard.entity.Worker;
import com.siteguard.exception.DatabaseOperationException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class WorkerRepository {

    private static final Logger logger = LoggerFactory.getLogger(WorkerRepository.class);

    public Worker save(Worker worker) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            session.persist(worker);
            tx.commit();
            logger.info("Saved worker with ID: {}", worker.getId());
            return worker;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            logger.error("Error saving worker: {}", worker.getName(), e);
            throw new DatabaseOperationException("Failed to save worker", e);
        }
    }

    public Optional<Worker> findById(Long id) {
        try (Session session = HibernateUtil.openSession()) {
            Worker worker = session.get(Worker.class, id);
            return Optional.ofNullable(worker);
        } catch (Exception e) {
            logger.error("Error finding worker by ID: {}", id, e);
            throw new DatabaseOperationException("Failed to fetch worker by ID: " + id, e);
        }
    }

    public List<Worker> findAll() {
        try (Session session = HibernateUtil.openSession()) {
            Query<Worker> query = session.createQuery("FROM Worker w ORDER BY w.id DESC", Worker.class);
            return query.list();
        } catch (Exception e) {
            logger.error("Error fetching all workers", e);
            throw new DatabaseOperationException("Failed to fetch all workers", e);
        }
    }

    public Optional<Worker> findByEmployeeCode(String employeeCode) {
        try (Session session = HibernateUtil.openSession()) {
            Query<Worker> query = session.createQuery(
                    "FROM Worker w WHERE w.employeeCode = :code", Worker.class);
            query.setParameter("code", employeeCode);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding worker by employee code: {}", employeeCode, e);
            throw new DatabaseOperationException("Failed to fetch worker by employee code", e);
        }
    }

    public List<Worker> findActiveWorkers() {
        try (Session session = HibernateUtil.openSession()) {
            Query<Worker> query = session.createQuery(
                    "FROM Worker w WHERE w.status = :status ORDER BY w.name ASC", Worker.class);
            query.setParameter("status", Worker.Status.ACTIVE);
            return query.list();
        } catch (Exception e) {
            logger.error("Error fetching active workers", e);
            throw new DatabaseOperationException("Failed to fetch active workers", e);
        }
    }

    public Worker update(Worker worker) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            Worker updatedWorker = session.merge(worker);
            tx.commit();
            logger.info("Updated worker with ID: {}", worker.getId());
            return updatedWorker;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            logger.error("Error updating worker ID: {}", worker.getId(), e);
            throw new DatabaseOperationException("Failed to update worker", e);
        }
    }

    public boolean deleteById(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            Worker worker = session.get(Worker.class, id);
            if (worker != null) {
                session.remove(worker);
                tx.commit();
                logger.info("Deleted worker with ID: {}", id);
                return true;
            }
            tx.commit();
            return false;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            logger.error("Error deleting worker with ID: {}", id, e);
            throw new DatabaseOperationException("Failed to delete worker with ID: " + id, e);
        }
    }
}
