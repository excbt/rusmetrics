package ru.excbt.datafuse.nmk.data.service;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.support.EntityColumn;
import ru.excbt.datafuse.nmk.service.QueryDSLService;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Service
public class DBMetadataService {

	private static final List<String> SKIP_COLUMNS;

	static {
		SKIP_COLUMNS = Collections.unmodifiableList(
				Arrays.asList("id", "created_date", "last_modified_date", "created_by", "last_modified_by"));
	}

	private final QueryDSLService queryDSLService;

	@Autowired
    public DBMetadataService(QueryDSLService queryDSLService) {
        this.queryDSLService = queryDSLService;
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


    private final static class SchemaPaths {

        private final static RelationalPath<Object> columnsPath = new RelationalPathBase<>(
            Object.class,
            "v",
            "information_schema",
            "columns");

        private final static StringPath columnName = Expressions.stringPath(columnsPath, "column_name");
        private final static StringPath dataType = Expressions.stringPath(columnsPath,"data_type");
        private final static StringPath tableSchema = Expressions.stringPath(columnsPath, "table_schema");
        private final static StringPath tableName = Expressions.stringPath(columnsPath, "table_name");

    }

	/**
	 *
	 * @param schemaName
	 * @param tableName
	 * @return
	 */

	public List<EntityColumn> selectTableEntityColumns(String schemaName, String tableName, boolean isSkipColumns) {
		List<EntityColumn> result;
        result = queryDSLService.doReturningWork((c) -> {
            SQLQuery<Tuple> query = new SQLQuery<>(c, QueryDSLService.templates);
            query.select(SchemaPaths.columnName, SchemaPaths.dataType).from(SchemaPaths.columnsPath)
                .where(SchemaPaths.tableSchema.eq(schemaName).and(SchemaPaths.tableName.eq(tableName)));
            List<Tuple> resultList = query.fetch();

            return resultList.stream()
                .map(i -> new EntityColumn(i.get(SchemaPaths.columnName), i.get(SchemaPaths.dataType)))
                .filter(ec -> !(isSkipColumns && SKIP_COLUMNS.contains(ec.getColumnName())))
                .collect(Collectors.toList());
        });

        return result;
	}

}
