package ru.excbt.datafuse.nmk.data.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLQueryFactory;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

@Service
public class DBSessionService {

    @PersistenceContext(unitName = "nmk-p")
    private EntityManager em;

    private final EntityManagerFactory entityManagerFactory;

    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    public DBSessionService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.jpaQueryFactory = new JPAQueryFactory(entityManagerFactory.createEntityManager());
    }

    public Session getSession() {
        if (em == null) {
            throw new IllegalStateException("Session Service is not available");
        }
        Session session = em.unwrap(Session.class);
        return session;
    }

    public EntityManager em() {
        return em;
    }

    public JPAQueryFactory jpaQueryFactory() {
        return jpaQueryFactory;
    }

}
