package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EntityColumn implements Comparable<EntityColumn> {

	public final static Comparator<EntityColumn> ENTITY_COLUMN_COMPARATOR = (c1, c2) -> c1.getColumnName()
			.compareTo(c2.getColumnName());

	private final String columnName;
	private final String dataType;

	public EntityColumn(String columnName, String dataType) {
		checkNotNull(columnName);
		this.columnName = columnName;
		this.dataType = dataType;
	}

	public EntityColumn(String columnName) {
		this.columnName = columnName;
		this.dataType = null;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getDataType() {
		return dataType;
	}

	@Override
	public int compareTo(EntityColumn o) {
		return this.columnName.compareTo(o.columnName);
	}

}
