/**
 *
 */
package ru.excbt.datafuse.nmk.data.service.support;

import org.postgresql.util.PSQLException;
import org.springframework.data.domain.Persistable;

import javax.persistence.PersistenceException;

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

    /**
     *
     * @param clazz
     * @param id
     * @return
     */
    public static <T extends Persistable<?>> PersistenceException entityNotFoundException (Class<T> clazz, Object id) {
        return entityNotFoundException(clazz, id, false);
    }

    /**
     *
     * @param clazz
     * @param id
     * @return
     */
    public static <T extends Persistable<?>> PersistenceException entityNotFoundException (Class<T> clazz, Object id, boolean keyname) {
        throw new PersistenceException("Entity " + clazz.getSimpleName() + " with " +
            (keyname ? "keyname" : "ID")
            +"=" + id + " is not found");
    }



}
