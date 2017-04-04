package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by kovtonyk on 24.03.2017.
 */
@JsonIgnoreProperties(ignoreUnknown =  true)
public class PDTableCellValuePack extends PDTableCell<PDTableCellValuePack> {

    public PDTableCellValuePack(){
        super();
        setCellType(PDCellType.VALUE_PACK);
    }

    public <T extends PDTableCell<T>> T createChildValue(final Class<T> valueType) {
        T child = this.getTablePart().createValueElement(valueType);
        childElements.add(child);
        child.parent = this;
        return child;
    }

    public <T extends PDTableCell<T>> T createSiblingValue(final Class<T> valueType) {
        checkState(parent != null);
        T sibling = this.getTablePart().createValueElement(valueType);
        parent.childElements.add(sibling);
        sibling.parent = parent;
        return sibling;
    }

}
