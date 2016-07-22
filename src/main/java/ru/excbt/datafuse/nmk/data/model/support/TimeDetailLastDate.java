package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;
import java.util.Date;

import ru.excbt.datafuse.nmk.data.model.markers.DataDateFormatter;

public class TimeDetailLastDate implements Serializable, DataDateFormatter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5090791752051900144L;

	public final String timeDetailType;
	public final Date dataDate;

	public TimeDetailLastDate(String timeDetailType, Date dataDate) {
		this.timeDetailType = timeDetailType;
		this.dataDate = dataDate;
	}

	@Override
	public Date getDataDate() {
		return dataDate;
	}

	@Override
	public String getTimeDetailType() {
		return timeDetailType;
	}

	@Override
	public String toString() {
		return String.format("TimeDetailLastDate [timeDetailType=%s, dataDate=%s]", timeDetailType, dataDate);
	}

}