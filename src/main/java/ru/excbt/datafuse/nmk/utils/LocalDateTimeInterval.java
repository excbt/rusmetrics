/**
 * 
 */
package ru.excbt.datafuse.nmk.utils;

import java.time.LocalDateTime;
import java.util.Date;

import org.apache.commons.lang3.tuple.Pair;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 11.01.2017
 * 
 */
public class LocalDateTimeInterval implements DateInterval {

	private final LocalDateTime fromDate;
	private final LocalDateTime toDate;

	/**
	 * @param fromDate
	 * @param toDate
	 */
	public LocalDateTimeInterval(LocalDateTime fromDate, LocalDateTime toDate) {
		super();
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	/**
	 * 
	 * @param datePairs
	 */
	public LocalDateTimeInterval(Pair<LocalDateTime, LocalDateTime> datePairs) {
		super();
		this.fromDate = datePairs.getLeft();
		this.toDate = datePairs.getRight();
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
