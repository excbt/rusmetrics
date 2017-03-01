/**
 * 
 */
package ru.excbt.datafuse.nmk.config.jpa;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 18.01.2017
 * 
 */
public interface DatabaseConnectionSettings {
	public String getDatasourceUrl();

	public String getDatasourceUsername();

	public String getDatasourcePassword();

}
