package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Класс для работы с запросами
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 17.12.2015
 *
 */
public class ColumnHelper {
	private final String[] columns;
	private final String operator;
	private final List<String> columnList;

	/**
	 * 
	 * @param columns
	 * @param operator
	 */
	public ColumnHelper(String[] columns, String operator) {
		checkNotNull(columns);
		this.columns = columns;
		this.operator = operator;
		this.columnList = Collections.unmodifiableList(Arrays.asList(columns));
	}

	/**
	 * 
	 * @param columns
	 */
	public ColumnHelper(String[] columns) {
		checkNotNull(columns);
		this.columns = columns;
		this.operator = null;
		this.columnList = Collections.unmodifiableList(Arrays.asList(columns));
	}

	/**
	 * 
	 * @param columns
	 * @return
	 */
	public static ColumnHelper newInstance(String[] columns) {
		return new ColumnHelper(columns);
	}

	/**
	 * 
	 * @return
	 */
	public String build() {
		StringBuilder sb = new StringBuilder();
		for (String col : columns) {
			sb.append(operator != null ? String.format(operator, col) : col);
			sb.append(" as ");
			sb.append(col);
			sb.append(',');
		}
		sb.delete(sb.length() - 1, sb.length());
		return sb.toString();
	}

	/**
	 * 
	 * @param column
	 * @return
	 */
	public int indexOf(String column) {
		return columnList.indexOf(column);
	}

	/**
	 * 
	 * @param results
	 * @param column
	 * @return
	 */
	public BigDecimal getResult(Object[] results, String column) {
		int idx = indexOf(column);
		checkState(idx >= 0, "Invalid column index");
		Object value = results[idx];
		return DBRowUtils.asBigDecimal(value);
	}

	/**
	 * 
	 * @param results
	 * @param column
	 * @param valueClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getResultAsClass(Object[] results, String column, Class<T> valueClass) {
		int idx = indexOf(column);
		checkNotNull(valueClass);
		checkState(idx >= 0, "Invalid column index");
		Object value = results[idx];

		if (value == null) {
			return null;
		}

		if (!(valueClass.isInstance(value))) {
			throw new IllegalArgumentException(String.format("Column %s is not type of String", column));
		}

		return (T) value;
	}
}
