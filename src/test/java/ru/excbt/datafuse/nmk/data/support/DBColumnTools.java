package ru.excbt.datafuse.nmk.data.support;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;

public class DBColumnTools extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(DBColumnTools.class);

	private static final String NEW_LINE = System.getProperty("line.separator");

	private static final List<String> SKIP_COLUMNS;
	private static final Map<String, String> DB_TYPE_MAP;

	static {
		SKIP_COLUMNS = Collections.unmodifiableList(
				Arrays.asList("id", "created_date", "last_modified_date", "created_by", "last_modified_by"));

		Map<String, String> m = new HashMap<>();
		m.put("bigint", "Long");
		m.put("text", "String");
		m.put("integer", "Integer");
		m.put("numeric", "BigDecimal");
		m.put("boolean", "Boolean");
		m.put("timestamp without time zone", "Date");
		m.put("uuid", "UUID");
		m.put("date", "Date");

		DB_TYPE_MAP = Collections.unmodifiableMap(m);
	}

	@PersistenceContext(unitName = "nmk-p")
	private EntityManager em;

	private static class EntityColumn {
		private String columnName;
		private String dataType;
	}

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

	private String parseDBName(String dbName) {
		return parseDBName(dbName, false);
	}

	/**
	 * 
	 * @param dbName
	 * @return
	 */
	private String parseDBName(String dbName, boolean initUpper) {
		checkNotNull(dbName);
		StringBuilder sb = new StringBuilder();
		char[] ch = dbName.toCharArray();
		boolean up = initUpper;
		for (int idx = 0; idx < ch.length; idx++) {
			if (ch[idx] == '_') {
				up = true;
			} else {
				if (up) {
					sb.append(Character.toUpperCase(ch[idx]));
					up = false;
				} else {
					sb.append(ch[idx]);
				}
			}
		}
		return sb.toString();
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
		logger.debug("Sql: {}", sqlString.toString());

		Query q1 = em.createNativeQuery(sqlString.toString());

		q1.setParameter("tableSchema", schemaName);
		q1.setParameter("tableName", tableName);

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
	private List<EntityColumn> getTableEntityProp(String schemaName, String tableName) {
		List<EntityColumn> result = new ArrayList<>();

		ColumnHolder columnHolder = new ColumnHolder(
				new String[] { "table_schema", "table_name", "column_name", "ordinal_position", "data_type" }, null);

		List<Object[]> resultColumns = columnQueryTool(columnHolder, schemaName, tableName);

		resultColumns.forEach(i -> {
			String columnName = columnHolder.getStringResult(i, "column_name");
			String dataType = columnHolder.getStringResult(i, "data_type");
			logger.info("columnName: {} {}", columnName, dataType);

			EntityColumn entityColumn = new EntityColumn();
			entityColumn.columnName = columnName;
			entityColumn.dataType = dataType;
			result.add(entityColumn);
		});

		return result;
	}

	private String classDefBuilder(String tableName, List<EntityColumn> entityColumns) {
		StringBuilder sb = new StringBuilder();

		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append("import java.util.UUID;");
		sb.append(NEW_LINE);
		sb.append("import java.util.Date;");
		sb.append(NEW_LINE);
		sb.append("import java.math.BigDecimal;");
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append("import javax.persistence.Column;");
		sb.append(NEW_LINE);
		sb.append("import javax.persistence.Entity;");
		sb.append(NEW_LINE);
		sb.append("import javax.persistence.Table;");
		sb.append(NEW_LINE);
		sb.append("import javax.persistence.Version;");
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);
		sb.append("import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;");
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);

		sb.append(NEW_LINE);
		sb.append("@Entity");
		sb.append(NEW_LINE);
		sb.append(String.format("@Table(schema = DBMetadata.SCHEME_PORTAL, name = \"%s\")", tableName));
		sb.append(NEW_LINE);
		sb.append(String.format("public class %s extends AbstractAuditableModel {", parseDBName(tableName, true)));
		sb.append(NEW_LINE);
		sb.append(NEW_LINE);

		entityColumns.forEach(i -> {

			if (SKIP_COLUMNS.contains(i.columnName)) {
				return;
			}

			if ("version".equals(i.columnName)) {
				sb.append("@Version");
				sb.append(NEW_LINE);
				sb.append("private int version;");
				sb.append(NEW_LINE);
				sb.append(NEW_LINE);
				return;
			}

			if ("deleted".equals(i.columnName)) {
				sb.append("@Column(name = \"deleted\")");
				sb.append(NEW_LINE);
				sb.append("private int deleted;");
				sb.append(NEW_LINE);
				sb.append(NEW_LINE);
				return;
			}

			String t = DB_TYPE_MAP.get(i.dataType);

			sb.append(String.format("@Column(name = \"%s\")", i.columnName));
			if ("UUID".equals(t)) {
				sb.append(NEW_LINE);
				sb.append("@org.hibernate.annotations.Type(type = \"org.hibernate.type.PostgresUUIDType\")");
			}
			sb.append(NEW_LINE);
			sb.append("private ");
			sb.append(t != null ? t : ("!!!!!" + i.dataType));
			sb.append(" ");
			sb.append(parseDBName(i.columnName));
			sb.append(';');
			sb.append(NEW_LINE);
			sb.append(NEW_LINE);
		});

		sb.append("}");
		sb.append(NEW_LINE);

		return sb.toString();
	}

	private String objDeepCopyBuilder(String srcObjName, String destObjName, List<EntityColumn> entityColumns) {
		StringBuilder sb = new StringBuilder();

		entityColumns.forEach(i -> {

			if (SKIP_COLUMNS.contains(i.columnName)) {
				return;
			}

			String propName = parseDBName(i.columnName, true);

			sb.append(destObjName);
			sb.append('.');
			sb.append("set");
			sb.append(propName);
			sb.append('(');
			sb.append(srcObjName);
			sb.append('.');
			sb.append("get");
			sb.append(propName);
			sb.append("()");
			sb.append(");");
			sb.append(NEW_LINE);
		});

		return sb.toString();
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDBColumns() throws Exception {
		String colName = "prop_name";
		String propName = parseDBName(colName);
		logger.info("prop: {}, column: {}", propName, colName);

		String tableName = "subscr_data_source_loading_settings";

		List<EntityColumn> entityColumns = getTableEntityProp("portal", tableName);
		assertTrue(entityColumns.size() > 0);

		String classDef = classDefBuilder(tableName, entityColumns);

		System.err.println(classDef);

	}

	@Test
	public void testDeepCopyColumns() throws Exception {
		String colName = "prop_name";
		String propName = parseDBName(colName);
		logger.info("prop: {}, column: {}", propName, colName);

		String tableName = "cont_service_data_el_cons";

		List<EntityColumn> entityColumns = getTableEntityProp("public", tableName);
		assertTrue(entityColumns.size() > 0);

		String deepCopy = objDeepCopyBuilder("src", "dst", entityColumns);

		System.err.println(deepCopy);

	}

}
