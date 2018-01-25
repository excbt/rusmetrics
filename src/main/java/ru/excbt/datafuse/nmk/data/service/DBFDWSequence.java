package ru.excbt.datafuse.nmk.data.service;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import ru.excbt.datafuse.nmk.service.QueryDSLService;

import java.util.concurrent.ConcurrentLinkedQueue;

public class DBFDWSequence {

    public static final String DEFAULT_HIBERNATE_SEQ_TABLE = "hibernate_seq_table";
    public static final String DEFAULT_HIBERNATE_SEQ_COLUMN = "a";

    private final QueryDSLService queryDSLService;

    private final int increment;

    private final ConcurrentLinkedQueue<Long> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();;

    private final Path path;

    private static class Path {
        private final RelationalPath<Object> userPath;
        private final NumberPath<Long> nextId;

        public Path(String schema, String sequenceTable, String column) {
            userPath = new RelationalPathBase<>(Object.class, "setTable", schema, sequenceTable);
            nextId =  Expressions.numberPath(Long.class, userPath, column);
        }
    }

    public DBFDWSequence(QueryDSLService queryDSLService, String schema, String tableName, String column, int increment) {
        this.path = new Path(schema, tableName, column);
        this.queryDSLService = queryDSLService;
        this.increment = increment;
    }

    public DBFDWSequence(QueryDSLService queryDSLService, String schema) {
        this.path = new Path(schema, DEFAULT_HIBERNATE_SEQ_TABLE, DEFAULT_HIBERNATE_SEQ_COLUMN);
        this.queryDSLService = queryDSLService;
        this.increment = 1;
    }

    public DBFDWSequence(QueryDSLService queryDSLService, String schema, int increment) {
        this.path = new Path(schema, DEFAULT_HIBERNATE_SEQ_TABLE, DEFAULT_HIBERNATE_SEQ_COLUMN);
        this.queryDSLService = queryDSLService;
        this.increment = increment;
    }

    /**
     *
     * @return
     */
    private Long nextId() {
        return queryDSLService.doReturningWork(c -> {
            SQLQuery<Long> query = new SQLQuery<>(c, QueryDSLService.templates);
            return query.select(path.nextId).from(path.userPath).fetchFirst();
        });
    }

    private void loadSequence () {
        Long id = nextId();
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
