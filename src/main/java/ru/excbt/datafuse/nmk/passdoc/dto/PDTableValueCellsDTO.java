package ru.excbt.datafuse.nmk.passdoc.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.passdoc.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        addValueCells(tableCells, false);
    }

    public void addValueCells(List<PDTableCell<?>> tableCells, boolean sortAfter) {
        Optional<?> check = tableCells.stream().filter(i -> i.getCellType() == PDCellType.STATIC).findAny();
        if (check.isPresent()) {
            throw new IllegalArgumentException();
        }
        tableCells.forEach(i -> elements.add(createValueDTO(i)));
        if (sortAfter) {
            sortElements();
        }
    }

    public void sortElements() {
        elements.sort(Comparator.comparing(PDValueDTO::get_complexIdx, String.CASE_INSENSITIVE_ORDER));
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

        } else if (PDTableCellValueBoolean.class.isAssignableFrom(pdTableCell.getClass())) {
            result = PDValueBooleanDTO.newInstance(pdTableCell);
        } else if (PDTableCellValueDate.class.isAssignableFrom(pdTableCell.getClass())) {
            result = PDValueDateDTO.newInstance(pdTableCell);
        }

        if (result == null) {
            throw new UnsupportedOperationException("Class " + pdTableCell.getClass().getSimpleName() + " is not supported yet");
        }

        result.cloneConstraints(pdTableCell);

        return result;
    }

    public boolean checkComplexIdxs() {
        return CellChecker.checkComplexIdx(elements);
    }

    public boolean checkConstraints() {
        boolean result = true;
        for (PDValueDTO valueDTO: elements) {
            if (!valueDTO.getConstraints().isEmpty()) {
                Optional<Boolean> failValue = valueDTO.getConstraints().stream().map((i) -> new PDValueConstraint(i.getValueSubtype()))
                    .map((i) -> i.check(valueDTO)).filter((i) -> Boolean.FALSE.equals(i)).findFirst();
                if (failValue.isPresent()) {
                    result = result && false;
                }
            }
        }
        return result;
    }

    public boolean performAllCheck() {
        return CellChecker.checkComplexIdx(elements) &&
            checkConstraints();
    }

    public List<String> complexIdxs() {
        return elements.stream().map(ComplexIdx::get_complexIdx).collect(Collectors.toList());
    }

}
