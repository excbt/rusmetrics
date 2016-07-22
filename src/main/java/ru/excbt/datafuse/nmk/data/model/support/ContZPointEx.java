package ru.excbt.datafuse.nmk.data.model.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

public class ContZPointEx extends ExtraInfo<ContZPoint> implements DeletableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3139573316888700353L;

	private DateTime lastDataDate;

	private Boolean dataExists;

	private final List<TimeDetailLastDate> timeDetailLastDates = new ArrayList<>();

	/**
	 * 
	 * @param srcObject
	 */
	public ContZPointEx(ContZPoint srcObject) {
		super(srcObject);
		this.dataExists = false;
	}

	/**
	 * 
	 * @param srcObject
	 */
	@Deprecated
	public ContZPointEx(ContZPoint srcObject, Boolean dataExists) {
		super(srcObject);
		this.dataExists = dataExists;
	}

	/**
	 * 
	 * @param srcObject
	 * @param lastDataDate
	 */
	@Deprecated
	public ContZPointEx(ContZPoint srcObject, DateTime lastDataDate) {
		super(srcObject);
		this.lastDataDate = lastDataDate;
		this.dataExists = lastDataDate != null;
	}

	@Deprecated
	public ContZPointEx(ContZPoint srcObject, Date lastDataDate) {
		super(srcObject);
		this.lastDataDate = lastDataDate != null ? new DateTime(lastDataDate) : null;
		this.dataExists = lastDataDate != null;
	}

	/**
	 * 
	 * @param srcObject
	 * @param timeDetailLastDates
	 */
	public ContZPointEx(ContZPoint srcObject, List<TimeDetailLastDate> timeDetailLastDates) {
		super(srcObject);
		this.lastDataDate = null;
		this.dataExists = timeDetailLastDates.size() > 0;
		this.timeDetailLastDates.addAll(timeDetailLastDates);
	}

	/**
	 * 
	 * @return
	 */
	public Date getLastDataDate() {
		if (timeDetailLastDates.size() > 0) {
			final MaxCheck<Date> maxCheck = new MaxCheck<>();

			timeDetailLastDates.forEach(i -> maxCheck.check(i.getDataDate()));

			return maxCheck.getObject();
		}

		return lastDataDate != null ? lastDataDate.toDate() : null;
	}

	public void setLastDataDate(Date lastDataDate) {
		if (lastDataDate != null) {
			this.lastDataDate = new DateTime(lastDataDate);
		} else {
			this.lastDataDate = null;
		}

	}

	public Boolean getDataExists() {
		return dataExists;
	}

	public void setDataExists(Boolean dataExists) {
		this.dataExists = dataExists;
	}

	@JsonIgnore
	@Override
	public int getDeleted() {
		return getObject() == null ? 0 : getObject().getDeleted();
	}

	@Override
	public void setDeleted(int deleted) {
		throw new UnsupportedOperationException();
	}

	public List<TimeDetailLastDate> getTimeDetailLastDates() {
		return Collections.unmodifiableList(timeDetailLastDates);
	}

}
