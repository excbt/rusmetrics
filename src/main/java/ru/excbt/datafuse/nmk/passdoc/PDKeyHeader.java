package ru.excbt.datafuse.nmk.passdoc;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


/**
 * Created by kovtonyk on 25.04.2017.
 */
@Builder

public class PDKeyHeader {

    public final static Mode mode = new Mode();

    public static class Mode {
        @Getter
        @Setter
        private boolean _shortKey;
    }

    @Getter
    private final String key;
    private final String header;

    public PDKeyHeader(String key, String header) {
        this.key = key;
        this.header = header;
    }

    public String getShortKey() {
        if (!mode._shortKey) {
            return key;
        }
        Preconditions.checkNotNull(key);
        String[] hh = key.split("\\.");
        String result = hh.length > 1 ? hh[1] : key;
        return result != null ? result : key;
    }


    public String getHeader() {
        return this.header;
    }
}
