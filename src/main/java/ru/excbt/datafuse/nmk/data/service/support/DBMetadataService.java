package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.support.EntityColumn;

@Service
public class DBMetadataService {

	private static final List<String> SKIP_COLUMNS;

	static {
		SKIP_COLUMNS = Collections.unmodifiableList(
				Arrays.asList("id", "created_date", "last_modified_date", "created_by", "last_modified_by"));
	}

	@PersistenceContext(unitName = "nmk-p")
	private EntityManager em;

	private static class ColumnHolder {
		private final String[] columns;
		private final String operator;
		private final List<String> columnList;

		public ColumnHolder(String[] columns, String operator) {
			checkNotNull(columns);
			this.columns = columns;
			this.operator = operator;
			this.columnList = Collections.unmodifiableList(Arrays.asList(columns));
		}

		public String build() {
			StringBuilder sb = new StringBuilder();
			for (String col : columns) {
				if (operator != null) {
					sb.append(String.format(operator, col));
					sb.append(" as ");
					sb.append(col);
					sb.append(',');
				} else {
					sb.append(col);
					sb.append(',');
				}
			}
			sb.delete(sb.length() - 1, sb.length());
			return sb.toString();
		}

		public int indexOf(String column) {
			return columnList.indexOf(column);
		}

		public String getStringResult(Object[] results, String column) {
			int idx = indexOf(column);
			checkState(idx >= 0, "Invalid column index");
			Object value = results[idx];
			return (String) value;

		}
	}

	/**
	 * 
	 * @param columnHelper
	 * @param schemaName
	 * @param tableName
	 * @return
	 */
	protected List<Object[]> columnQueryTool(ColumnHolder columnHolder, String schemaName, String tableName) {

		StringBuilder sqlString = new StringBuilder();
		sqlString.append(" SELECT ");
		sqlString.append(columnHolder.build());
		sqlString.append(" FROM information_schema.columns ");
		sqlString.append(" WHERE table_schema = :tableSchema ");
		sqlString.append(" AND table_name = :tableName ");
		sqlString.append(" ORDER BY ordinal_position");

		Query q1 = em.createNativeQuery(sqlString.toString());

		q1.setParameter("tableSchema", schemaName);
		q1.setParameter("tableName", tableName);

		@SuppressWarnings("unchecked")
		List<Object[]> result = q1.getResultList();
		checkNotNull(result);

		return result;
	}

	/**
	 * 
	 * @param schemaName
	 * @param tableName
	 * @return
	 */

	public List<EntityColumn> selectTableEntityColumns(String schemaName, String tableName, boolean isSkipColumns) {
		List<EntityColumn> result = new ArrayList<>();

		ColumnHolder columnHolder = new ColumnHolder(
				new String[] { "table_schema", "table_name", "column_name", "ordinal_position", "data_type" }, null);

		List<Object[]> resultColumns = columnQueryTool(columnHolder, schemaName, tableName);

		resultColumns.forEach(i -> {
			String columnName = columnHolder.getStringResult(i, "column_name");
			String dataType = columnHolder.getStringResult(i, "data_type");

			if (isSkipColumns && SKIP_COLUMNS.contains(columnName)) {
				return;
			}

			EntityColumn entityColumn = new EntityColumn(columnName, dataType);
			result.add(entityColumn);
		});

		return result;
	}

}
