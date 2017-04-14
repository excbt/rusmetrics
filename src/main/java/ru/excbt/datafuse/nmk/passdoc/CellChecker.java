package ru.excbt.datafuse.nmk.passdoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kovtonyk on 05.04.2017.
 */
public final class CellChecker {

    private static final Logger log = LoggerFactory.getLogger(CellChecker.class);

    private CellChecker() {
    }

    public static <T extends ComplexIdx> boolean checkComplexIdx(List<T> cells){
        Set<String> keys = new HashSet<>();

        for (T cell: cells) {
            boolean pass = keys.add(cell.get_complexIdx());
            if (!pass) {
                log.warn("complexIdx: {} exists. Object: ", cell.get_complexIdx(), cell.toString());
                return false;
            }
        }
        return true;
    }
}
