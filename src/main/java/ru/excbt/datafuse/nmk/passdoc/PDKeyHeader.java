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
        private boolean _shortHeader;
    }

    @Getter
    private final String key;
    private final String fullHeader;

    public PDKeyHeader(String key, String fullHeader) {
        this.key = key;
        this.fullHeader = fullHeader;
    }

    public String getHeader() {
        if (!mode._shortHeader) {
            return fullHeader;
        }
        Preconditions.checkNotNull(fullHeader);
        String[] hh = fullHeader.split(".");
        return hh.length > 1 ? hh[1] : fullHeader;
    }
}
