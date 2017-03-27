package ru.excbt.datafuse.nmk.passdoc;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by kovtonyk on 27.03.2017.
 */
@Getter
@Setter
public class PDCellValuesDTO {

    private final List<PDTableCell> elements = new ArrayList<>();

    private int version;

    private String tableKey;

    public void addTableCellValues(List<PDTableCell> tableCells) {
        Optional<?> check = tableCells.stream().filter(i -> i.getCellType() == PDCellType.STATIC).findAny();
        if (check.isPresent()) {
            throw new IllegalArgumentException();
        }
        elements.addAll(tableCells);
    }
}
