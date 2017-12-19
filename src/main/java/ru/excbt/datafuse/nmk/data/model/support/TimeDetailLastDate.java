package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import ru.excbt.datafuse.nmk.data.model.markers.DataDateFormatter;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

public class TimeDetailLastDate implements Serializable, DataDateFormatter {

	/**
	 *
	 */
	private static final long serialVersionUID = -5090791752051900144L;

	public static final String ALL = "ALL";

	public final String timeDetailType;

	public final LocalDateTime dataDateTime;

	public TimeDetailLastDate(String timeDetailType, LocalDateTime dataDateTime) {
		this.timeDetailType = timeDetailType;
		this.dataDateTime = dataDateTime;
	}

	@Override
	public Date getDataDate() {
		return LocalDateUtils.asDate(dataDateTime);
	}

	@Override
	public String getTimeDetailType() {
		return timeDetailType;
	}

	@Override
	public String toString() {
		return String.format("TimeDetailLastDate [timeDetailType=%s, dataDateTime=%s]", timeDetailType, dataDateTime);
	}

}
