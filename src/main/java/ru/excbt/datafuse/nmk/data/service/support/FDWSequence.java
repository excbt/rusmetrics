package ru.excbt.datafuse.nmk.data.service.support;

import ru.excbt.datafuse.nmk.service.utils.DBRowUtil;

import javax.persistence.Query;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FDWSequence {

    private final String sql;

    private final SessionService sessionService;

    private final int increment;

    private final ConcurrentLinkedQueue<Long> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();;

    public FDWSequence(SessionService sessionService, String sql) {
        this.sql = sql;
        this.sessionService = sessionService;
        this.increment = 1;
    }

    public FDWSequence(SessionService sessionService, String sql, int increment) {
        this.sql = sql;
        this.sessionService = sessionService;
        this.increment = increment;
    }

    private void loadSequence () {
        Query qry = sessionService.getSession().createNativeQuery(sql);
        Long id = DBRowUtil.asLong(qry.getSingleResult());
        for (int i = 0; i < increment; i++) {
            concurrentLinkedQueue.add(id + i);
        }
    }

    public Long next() {
        Long id;
        while (true) {
            id = concurrentLinkedQueue.poll();
            if (id != null) break;

            loadSequence();
        }
        return id;
    }


}
