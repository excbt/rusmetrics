package ru.excbt.datafuse.nmk.data.model.markers;

import java.util.Date;

import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.utils.DateFormatUtils;

public interface DataDateFormatter extends TimeDetailTypeObject {
	public Date getDataDate();

	/**
	 *
	 * @return
	 */
	default String getDataDateString() {

		TimeDetailKey timeDetailKey = TimeDetailKey.searchKeyname(getTimeDetailType());
		if (timeDetailKey != null && timeDetailKey.isTruncDate()) {
			return DateFormatUtils.formatDateTime(this.getDataDate(), DateFormatUtils.DATE_FORMAT_STR_TRUNC);
		}

		return DateFormatUtils.formatDateTime(this.getDataDate(), DateFormatUtils.DATE_FORMAT_STR_FULL);
	}

}
