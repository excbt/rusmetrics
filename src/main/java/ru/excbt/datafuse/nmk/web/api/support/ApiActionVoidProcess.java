/**
 * 
 */
package ru.excbt.datafuse.nmk.web.api.support;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.02.2016
 * 
 */
public interface ApiActionVoidProcess extends ApiActionProcess<Void> {

	/**
	 * 
	 */
	public void process();

	/**
	 * 
	 */
	@Override
	default Void processAndReturnResult() {
		process();
		return null;
	}

}
