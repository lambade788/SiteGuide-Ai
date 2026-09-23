package com.siteguard.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {

    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static final SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration().configure("hibernate.cfg.xml");

            // Environment variable overrides for secure database credentials
            String envDbUrl = System.getenv("DB_URL");
            String envDbUser = System.getenv("DB_USERNAME");
            String envDbPass = System.getenv("DB_PASSWORD");

            if (envDbUrl != null && !envDbUrl.trim().isEmpty()) {
                configuration.setProperty("hibernate.connection.url", envDbUrl.trim());
            }
            if (envDbUser != null && !envDbUser.trim().isEmpty()) {
                configuration.setProperty("hibernate.connection.username", envDbUser.trim());
            }
            if (envDbPass != null) {
                configuration.setProperty("hibernate.connection.password", envDbPass);
            }

            sessionFactory = configuration.buildSessionFactory();
            logger.info("Hibernate SessionFactory initialized successfully.");

        } catch (Throwable ex) {
            logger.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session openSession() {
        return getSessionFactory().openSession();
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            logger.info("Hibernate SessionFactory shutdown completed.");
        }
    }
}