package ru.excbt.datafuse.nmk.data.model.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContZPointVO extends ModelWrapper<ContZPoint> implements DeletedMarker {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3139573316888700353L;

	@Deprecated
	private final DateTime lastDataDate;

	private final Boolean dataExists;

	private final List<TimeDetailLastDate> timeDetailLastDates = new ArrayList<>();

	/**
	 * 
	 * @param srcObject
	 */
	public ContZPointVO(ContZPoint srcObject) {
		super(srcObject);
		this.dataExists = false;
		this.lastDataDate = null;
	}

	/**
	 * 
	 * @param srcObject
	 * @param lastDataDate
	 */
	public ContZPointVO(ContZPoint srcObject, DateTime lastDataDate) {
		super(srcObject);
		this.lastDataDate = lastDataDate;
		this.dataExists = lastDataDate != null;
	}

	public ContZPointVO(ContZPoint srcObject, Date lastDataDate) {
		super(srcObject);
		this.lastDataDate = lastDataDate != null ? new DateTime(lastDataDate) : null;
		this.dataExists = lastDataDate != null;
	}

	/**
	 * 
	 * @param srcObject
	 * @param timeDetailLastDates
	 */
	public ContZPointVO(ContZPoint srcObject, List<TimeDetailLastDate> timeDetailLastDates) {
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

	public Boolean getDataExists() {
		return dataExists;
	}

	@JsonIgnore
	@Override
	public int getDeleted() {
		return getObject() == null ? 0 : getObject().getDeleted();
	}

	public List<TimeDetailLastDate> getTimeDetailLastDates() {
		return Collections.unmodifiableList(timeDetailLastDates);
	}
}
