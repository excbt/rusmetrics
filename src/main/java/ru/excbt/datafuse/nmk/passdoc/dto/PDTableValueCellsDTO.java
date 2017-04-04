package ru.excbt.datafuse.nmk.passdoc.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.passdoc.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by kovtonyk on 27.03.2017.
 */
@Getter
@Setter
public class PDTableValueCellsDTO {

    private final List<PDValueDTO> elements = new ArrayList<>();

    private int version;

    private String sectionKey;

    public void addValueCells(List<PDTableCell<?>> tableCells) {
        Optional<?> check = tableCells.stream().filter(i -> i.getCellType() == PDCellType.STATIC).findAny();
        if (check.isPresent()) {
            throw new IllegalArgumentException();
        }
        tableCells.forEach(i -> elements.add(createValueDTO(i)));
    }


    public static PDValueDTO createValueDTO(PDTableCell<?> pdTableCell) {
        PDValueDTO result = null;
        if (PDTableCellValueInteger.class.isAssignableFrom(pdTableCell.getClass())) {
            result = PDValueIntegerDTO.newInstance(pdTableCell);

        } else if (PDTableCellValueDouble.class.isAssignableFrom(pdTableCell.getClass())) {
            result = PDValueDoubleDTO.newInstance(pdTableCell);

        } else if (PDTableCellValueString.class.isAssignableFrom(pdTableCell.getClass())) {
            result = PDValueStringDTO.newInstance(pdTableCell);

        } else if (PDTableCellValueDoubleAggregation.class.isAssignableFrom(pdTableCell.getClass())) {
            result = PDValueDoubleAggregationDTO.newInstance(pdTableCell);

        } else if (PDTableCellValueCounter.class.isAssignableFrom(pdTableCell.getClass())) {
            result = PDValueCounterDTO.newInstance(pdTableCell);
        }

        if (result == null) {
            throw new UnsupportedOperationException("Class " + pdTableCell.getClass().getSimpleName() + " is not supported yet");
        }

        return result;
    }

}
