/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service.support;

import org.postgresql.util.PSQLException;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.12.2016
 * 
 */
public class DBExceptionUtils {

	private DBExceptionUtils() {

	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	public static PSQLException getPSQLException(Exception e) {

		PSQLException pe = null;

		Throwable t = e;
		while (t != null) {
			if (t instanceof PSQLException) {
				pe = (PSQLException) t;
				break;
			}
			t = t.getCause();
		}
		return pe;
	}

}
