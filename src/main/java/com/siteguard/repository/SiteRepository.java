package com.siteguard.repository;

import com.siteguard.config.HibernateUtil;
import com.siteguard.entity.Site;
import com.siteguard.exception.DatabaseOperationException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class SiteRepository {

    private static final Logger logger = LoggerFactory.getLogger(SiteRepository.class);

    public Site save(Site site) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            session.persist(site);
            tx.commit();
            return site;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            logger.error("Error saving site", e);
            throw new DatabaseOperationException("Failed to save site", e);
        }
    }

    public Optional<Site> findById(Long id) {
        try (Session session = HibernateUtil.openSession()) {
            return Optional.ofNullable(session.get(Site.class, id));
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch site by ID: " + id, e);
        }
    }

    public List<Site> findAll() {
        try (Session session = HibernateUtil.openSession()) {
            return session.createQuery("FROM Site s ORDER BY s.id DESC", Site.class).list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch all sites", e);
        }
    }

    public List<Site> findByProjectId(Long projectId) {
        try (Session session = HibernateUtil.openSession()) {
            Query<Site> query = session.createQuery("FROM Site s WHERE s.project.id = :pid", Site.class);
            query.setParameter("pid", projectId);
            return query.list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch sites by project ID: " + projectId, e);
        }
    }

    public Site update(Site site) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            Site updated = session.merge(site);
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DatabaseOperationException("Failed to update site", e);
        }
    }

    public boolean deleteById(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            Site site = session.get(Site.class, id);
            if (site != null) {
                session.remove(site);
                tx.commit();
                return true;
            }
            tx.commit();
            return false;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DatabaseOperationException("Failed to delete site ID: " + id, e);
        }
    }
}
