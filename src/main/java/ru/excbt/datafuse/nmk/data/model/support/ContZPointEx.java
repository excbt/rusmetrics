package ru.excbt.datafuse.nmk.data.model.support;

import java.util.Date;

import org.joda.time.DateTime;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;

public class ContZPointEx extends ExtraInfo<ContZPoint> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3139573316888700353L;

	private DateTime lastDataDate;

	/**
	 * 
	 * @param srcObject
	 */
	public ContZPointEx(ContZPoint srcObject) {
		super(srcObject);
	}

	/**
	 * 
	 * @param srcObject
	 * @param lastDataDate
	 */
	public ContZPointEx(ContZPoint srcObject, DateTime lastDataDate) {
		super(srcObject);
		this.lastDataDate = lastDataDate;
	}

	public ContZPointEx(ContZPoint srcObject, Date lastDataDate) {
		super(srcObject);
		this.lastDataDate = lastDataDate != null ? new DateTime(lastDataDate)
				: null;
	}

	public Date getLastDataDate() {
		return lastDataDate != null ? lastDataDate.toDate() : null;
	}

	public void setLastDataDate(Date lastDataDate) {
		if (lastDataDate != null) {
			this.lastDataDate = new DateTime(lastDataDate);	
		} else {
			this.lastDataDate = null;
		}
		
	}

}
