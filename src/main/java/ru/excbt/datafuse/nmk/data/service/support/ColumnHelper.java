package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ColumnHelper {
	private final String[] columns;
	private final String operator;
	private final List<String> columnList;

	public ColumnHelper(String[] columns, String operator) {
		checkNotNull(columns);
		this.columns = columns;
		this.operator = operator;
		this.columnList = Collections.unmodifiableList(Arrays.asList(columns));
	}

	public String build() {
		StringBuilder sb = new StringBuilder();
		for (String col : columns) {
			sb.append(String.format(operator, col));
			sb.append(" as ");
			sb.append(col);
			sb.append(',');
		}
		sb.delete(sb.length() - 1, sb.length());
		return sb.toString();
	}

	public int indexOf(String column) {
		return columnList.indexOf(column);
	}

	public BigDecimal getResult(Object[] results, String column) {
		int idx = indexOf(column);
		checkState(idx >= 0, "Invalid column index");
		Object value = results[idx];
		return DBRowUtils.asBigDecimal(value);

	}
}
