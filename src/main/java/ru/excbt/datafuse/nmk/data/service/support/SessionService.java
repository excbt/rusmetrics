package ru.excbt.datafuse.nmk.data.service.support;

import org.hibernate.Session;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class SessionService {

    @PersistenceContext(unitName = "nmk-p")
    private EntityManager em;

    public Session getSession() {
        if (em == null) {
            throw new IllegalStateException("Session Service is not available");
        }
        Session session = em.unwrap(Session.class);
        return session;
    }

}
