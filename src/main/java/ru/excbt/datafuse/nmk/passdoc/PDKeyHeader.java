package ru.excbt.datafuse.nmk.passdoc;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by kovtonyk on 25.04.2017.
 */
@Builder
@Getter

public class PDKeyHeader {
    private final String key;
    private final String header;

    public PDKeyHeader(String key, String header) {
        this.key = key;
        this.header = header;
    }
}
