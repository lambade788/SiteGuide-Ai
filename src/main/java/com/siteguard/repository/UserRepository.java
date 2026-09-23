package com.siteguard.repository;

import com.siteguard.config.HibernateUtil;
import com.siteguard.entity.User;
import com.siteguard.exception.DatabaseOperationException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    public User save(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            return user;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            logger.error("Error saving user: {}", user.getEmail(), e);
            throw new DatabaseOperationException("Failed to save user", e);
        }
    }

    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.openSession()) {
            return Optional.ofNullable(session.get(User.class, id));
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch user by ID: " + id, e);
        }
    }

    public Optional<User> findByEmail(String email) {
        try (Session session = HibernateUtil.openSession()) {
            Query<User> query = session.createQuery("FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch user by email: " + email, e);
        }
    }

    public List<User> findAll() {
        try (Session session = HibernateUtil.openSession()) {
            return session.createQuery("FROM User u ORDER BY u.id DESC", User.class).list();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to fetch all users", e);
        }
    }

    public User update(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            User updated = session.merge(user);
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DatabaseOperationException("Failed to update user", e);
        }
    }

    public boolean deleteById(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.openSession()) {
            tx = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                tx.commit();
                return true;
            }
            tx.commit();
            return false;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DatabaseOperationException("Failed to delete user ID: " + id, e);
        }
    }
}
