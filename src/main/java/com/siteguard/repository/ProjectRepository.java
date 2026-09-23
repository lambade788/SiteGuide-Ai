package com.siteguard.repository;

import com.siteguard.config.HibernateUtil;
import com.siteguard.entity.Project;
import com.siteguard.exception.DatabaseOperationException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ProjectRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProjectRepository.class);

    public Project save(Project project) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            session.persist(project);
            tx.commit();
            return project;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            logger.error("Error saving project", e);
            throw new DatabaseOperationException("Failed to save project", e);
        }
    }

    public Optional<Project> findById(Long id) {
        try (Session session = HibernateUtil.openSession()) {
            return Optional.ofNullable(session.get(Project.class, id));
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch project by ID: " + id, e);
        }
    }

    public List<Project> findAll() {
        try (Session session = HibernateUtil.openSession()) {
            return session.createQuery("FROM Project p ORDER BY p.id DESC", Project.class).list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch projects", e);
        }
    }

    public List<Project> findActiveProjects() {
        try (Session session = HibernateUtil.openSession()) {
            Query<Project> query = session.createQuery("FROM Project p WHERE p.status = :status", Project.class);
            query.setParameter("status", Project.Status.ACTIVE);
            return query.list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch active projects", e);
        }
    }

    public Project update(Project project) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            Project updated = session.merge(project);
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DatabaseOperationException("Failed to update project", e);
        }
    }

    public boolean deleteById(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            Project p = session.get(Project.class, id);
            if (p != null) {
                session.remove(p);
                tx.commit();
                return true;
            }
            tx.commit();
            return false;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DatabaseOperationException("Failed to delete project ID: " + id, e);
        }
    }
}
