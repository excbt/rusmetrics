/**
 *
 */
package ru.excbt.datafuse.nmk.service.utils;

import org.postgresql.util.PSQLException;
import org.springframework.data.domain.Persistable;
import org.springframework.security.access.AccessDeniedException;

import javax.persistence.PersistenceException;

/**
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.12.2016
 */
public class DBExceptionUtil {

    private DBExceptionUtil() {

    }

    /**
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

    public static String getPSQLExceptionMessage(Exception e) {
        PSQLException pe = getPSQLException(e);
        String sqlExceptiomMessage = pe != null ? pe.getMessage() : e.getMessage();
        return sqlExceptiomMessage;
    }


    /**
     * @param clazz
     * @param id
     * @return
     */
    public static <T> PersistenceException entityNotFoundException(Class<T> clazz, Object id) {
        return entityNotFoundException(clazz, id, false);
    }

    /**
     * @param clazz
     * @param id
     * @return
     */
    public static <T> PersistenceException entityNotFoundException(Class<T> clazz, Object id, boolean idKeyname) {
        throw new PersistenceException("Entity " + clazz.getSimpleName() + " with " +
            (idKeyname ? "keyname" : "ID")
            + "=" + id + " is not found");
    }


    /**
     *
     * @param clazz
     * @param id
     * @param keyname
     * @param <T>
     * @return
     */
    public static <T extends Persistable<?>> PersistenceException entityNotFoundException(Class<T> clazz, Object id, String keyname) {
        throw new PersistenceException("Entity " + clazz.getSimpleName() + " with " +
            keyname + "=" + id + " is not found");
    }


    /**
     * @param clazz
     * @param id
     * @param <T>
     * @return
     */
    public static <T extends Persistable<?>> AccessDeniedException accessDeniedException(Class<T> clazz, Object id) {
        throw new AccessDeniedException("Can not access entity " + clazz.getSimpleName() + " with ID = " + id);
    }


    public static <T> PersistenceException newEntityNotFoundException(Class<T> clazz, Object id, boolean idKeyname) {
        return new PersistenceException("Entity " + clazz.getSimpleName() + " with " +
            (idKeyname ? "keyname" : "ID")
            + "=" + id + " is not found");
    }

    public static <T> PersistenceException newEntityNotFoundException(Class<T> clazz, Object id) {
        return newEntityNotFoundException(clazz, id, false);
    }


}
