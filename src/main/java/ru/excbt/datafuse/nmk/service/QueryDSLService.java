package ru.excbt.datafuse.nmk.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class QueryDSLService {

    private static final Logger log = LoggerFactory.getLogger(QueryDSLService.class);

    @PersistenceContext(unitName = "nmk-p")
    private EntityManager em;

    private AtomicReference<JPAQueryFactory> jpaQueryFactoryRef = new AtomicReference<>();

    @PostConstruct
    private void postInit() {
        JPAQueryFactory check = getInstance2();
        log.debug("QueryDSLService initialized: {}", check != null);

    }

    /**
     *
     * @return
     */
    public JPAQueryFactory queryFactory() {
        return getInstance2();
    }

//    private JPAQueryFactory getInstance() {
//        if (em == null) {
//            throw new IllegalStateException("EntityManager is not initialized");
//        }
//        JPAQueryFactory result = jpaQueryFactory;
//        if (result == null) {
//            synchronized (this) {
//                result = jpaQueryFactory;
//                if (result == null) {
//                    jpaQueryFactory = result = new JPAQueryFactory(em);
//                }
//            }
//        }
//        return result;
//    }

    /**
     *
     * @return
     */
    private JPAQueryFactory getInstance2() {
        JPAQueryFactory result = jpaQueryFactoryRef.get();
        if (result == null) {
            if (em == null) {
                throw new IllegalStateException("EntityManager is not initialized");
            }
            synchronized (this) {
                result = new JPAQueryFactory(em);
                if (jpaQueryFactoryRef.compareAndSet(null, result)) {
                    return result;
                } else {
                    return jpaQueryFactoryRef.get();
                }
            }
        }
        return result;

    };

}
