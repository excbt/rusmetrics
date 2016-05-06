package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.SingularAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс для работы с запросами
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 17.12.2015
 *
 */
public class ColumnHelper {

	private static final Logger logger = LoggerFactory.getLogger(ColumnHelper.class);

	private final String[] columns;
	private final String operator;
	private final List<String> columnList;
	private final List<SingularAttribute<?, ?>> sAttributes;

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
		this.sAttributes = null;
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
		this.sAttributes = null;
	}

	/**
	 * 
	 * @param attributes
	 */
	//	public ColumnHelper(SingularAttribute<?, ?>[] attributes) {
	//		checkNotNull(attributes);
	//
	//		String[] colArray = new String[attributes.length];
	//		int i = 0;
	//		for (SingularAttribute<?, ?> col : attributes) {
	//			colArray[i++] = col.getName();
	//		}
	//
	//		this.columns = colArray;
	//		this.operator = null;
	//		this.columnList = Collections.unmodifiableList(Arrays.asList(colArray));
	//	}

	public ColumnHelper(SingularAttribute<?, ?>... attributes) {
		checkNotNull(attributes);

		String[] colArray = new String[attributes.length];
		int i = 0;
		final List<SingularAttribute<?, ?>> sattrs = new ArrayList<>();

		for (SingularAttribute<?, ?> col : attributes) {
			colArray[i++] = col.getName();
			sattrs.add(col);
		}

		this.columns = colArray;
		this.operator = null;
		this.columnList = Collections.unmodifiableList(Arrays.asList(colArray));
		this.sAttributes = Collections.unmodifiableList(sattrs);
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
	 * @param column
	 * @return
	 */
	public int indexOf(SingularAttribute<?, ?> column) {
		return columnList.indexOf(column.getName());
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

	/**
	 * 
	 * @param tuple
	 * @param column
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getTupleValue(Tuple tuple, SingularAttribute<?, ? super T> column) {
		checkNotNull(tuple);
		checkNotNull(column);

		int idx = indexOf(column);
		checkState(idx >= 0, "Invalid column index");

		//Object value = tuple.get(idx, column.getBindableJavaType());
		Object value = tuple.get(idx);

		if (value == null) {
			return null;
		}

		if (!column.getJavaType().isInstance(value)) {
			throw new IllegalArgumentException(
					String.format("Can't extract value from column %s. Actual class %s is not compatible with value %s",
							column.getName(), column.getJavaType().getSimpleName(), value.getClass().getSimpleName()));
		}

		return (T) value;
	}

	/**
	 * 
	 * @return
	 */
	public <T> Selection<?>[] getSelection(Root<T> root) {
		checkState(sAttributes != null);

		List<Selection<T>> result = new ArrayList<>();
		for (SingularAttribute<?, ?> sa : sAttributes) {
			result.add(root.get(sa.getName()));
		}
		return result.toArray(new Selection<?>[0]);
	}

}
