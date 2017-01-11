/**
 * 
 */
package ru.excbt.datafuse.nmk.utils;

import java.time.LocalDate;
import java.util.Date;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 11.01.2017
 * 
 */
public class LocalDateInterval implements DateInterval {

	private final LocalDate fromDate;
	private final LocalDate toDate;

	/**
	 * @param fromDate
	 * @param toDate
	 */
	public LocalDateInterval(LocalDate fromDate, LocalDate toDate) {
		super();
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	/* (non-Javadoc)
	 * @see ru.excbt.datafuse.nmk.utils.DateInterval#getFromDate()
	 */
	@Override
	public Date getFromDate() {
		return fromDate != null ? LocalDateUtils.asDate(fromDate) : null;
	}

	/* (non-Javadoc)
	 * @see ru.excbt.datafuse.nmk.utils.DateInterval#getToDate()
	 */
	@Override
	public Date getToDate() {
		return toDate != null ? LocalDateUtils.asDate(toDate) : null;
	}

	/* (non-Javadoc)
	 * @see ru.excbt.datafuse.nmk.utils.DateInterval#isValid()
	 */
	@Override
	public boolean isValid() {
		if (fromDate == null || toDate == null) {
			return false;
		}
		return fromDate.isBefore(toDate);
	}

	/* (non-Javadoc)
	 * @see ru.excbt.datafuse.nmk.utils.DateInterval#isValidEq()
	 */
	@Override
	public boolean isValidEq() {
		if (fromDate == null || toDate == null) {
			return false;
		}
		return fromDate.isBefore(toDate) || fromDate.isEqual(toDate);
	}

}
