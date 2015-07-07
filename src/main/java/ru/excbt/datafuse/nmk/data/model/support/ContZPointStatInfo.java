package ru.excbt.datafuse.nmk.data.model.support;

import java.util.Date;

import org.joda.time.DateTime;

public class ContZPointStatInfo {

	private final Long contZPoint;

	private final DateTime lastDataDate;

	private final Boolean dataExists;

	public ContZPointStatInfo(Long contZPoint, Date lastDataDate) {
		this.contZPoint = contZPoint;
		this.lastDataDate = lastDataDate == null ? null : new DateTime(
				lastDataDate);
		this.dataExists = lastDataDate != null;
	}

	public Date getLastDataDate() {
		return lastDataDate == null ? null : lastDataDate.toDate();
	}

	public Boolean getDataExists() {
		return dataExists;
	}

	public Long getContZPoint() {
		return contZPoint;
	}

}
