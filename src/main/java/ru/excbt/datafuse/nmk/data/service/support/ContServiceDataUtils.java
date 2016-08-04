package ru.excbt.datafuse.nmk.data.service.support;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.excbt.datafuse.nmk.data.model.support.TimeDetailLastDate;

public final class ContServiceDataUtils {

	private ContServiceDataUtils() {

	}

	/**
	 * Every row must contains:<br>
	 * <b>
	 * row[0] - ID (contZPoint),<br>
	 * row[1] - String (contServiceType),<br>
	 * row[2] - java.sql.Timestamp (dataDate)<br>
	 * </b>
	 * <p>
	 * Function makes map (contZPointId, List(contServceType, dataDate))
	 * 
	 * @param rowList
	 * @return
	 */
	public static final HashMap<Long, List<TimeDetailLastDate>> collectContZPointTimeDetailTypes(
			List<Object[]> rowList) {

		HashMap<Long, List<TimeDetailLastDate>> resultMap = new HashMap<>();

		for (Object[] row : rowList) {

			Long id = DBRowUtils.asLong(row[0]);
			String timeDetail = DBRowUtils.asString(row[1]);
			Timestamp lastDate = DBRowUtils.asTimestamp(row[2]);

			List<TimeDetailLastDate> list = resultMap.get(id);
			if (list == null) {
				list = new ArrayList<>();
				resultMap.put(id, list);
			}

			TimeDetailLastDate item = new TimeDetailLastDate(timeDetail, lastDate);
			list.add(item);
		}

		return resultMap;
	}

}
